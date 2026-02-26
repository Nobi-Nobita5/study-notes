# 信贷风控实时特征数仓最佳实践

## 实时数仓：行存 vs 列存选择

### **结论：混合使用（行列共存）** ⭐

```
实时数仓特点：
1. 高频实时写入（秒级/分钟级）
2. 既有点查需求（订单详情）
3. 又有分析需求（实时大屏）

推荐方案：
- 明细层（DWD）：行存表（支持高频写入 + 点查）
- 汇总层（DWS）：列存表（支持快速聚合分析）
- 宽表层：行存表（点查）
```

**结论：点查 = 行存，聚合 = 列存**

```
点查（主键查询）= 行存
  ↓
无论表多大（100 行 or 10 亿行）
```

---



## 一、业务场景分析

### **信贷风控特点**
```
1. 实时性要求极高
   - 申请审批：< 3秒响应
   - 欺诈检测：毫秒级
   
2. 特征工程复杂
   - 时间窗口特征（7天/30天/90天）
   - 关系图谱特征（设备/IP/联系人）
   - 行为序列特征
   
3. 数据特点
   - 高频查询：每秒数千次特征查询
   - 实时更新：用户行为实时入库
   - 历史回溯：需要保留完整明细
```

---

## 二、架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                      数据源层                                │
├─────────────────────────────────────────────────────────────┤
│ App埋点 │ 信贷系统 │ 三方数据 │ 设备指纹 │ 反欺诈平台 │
└────┬────────┬────────┬─────────┬──────────┬──────────────┘
     │        │        │         │          │
     ▼        ▼        ▼         ▼          ▼
┌─────────────────────────────────────────────────────────────┐
│                   Flink CDC / DataHub                        │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Holo ODS/DWD 层（行存）                     │
├─────────────────────────────────────────────────────────────┤
│ • 申请明细表（行存）- 支持实时写入和点查                      │
│ • 行为日志表（行存）- 高频写入                               │
│ • 设备指纹表（行存）- 关系图谱查询                           │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Flink 实时特征计算引擎                          │
├─────────────────────────────────────────────────────────────┤
│ • 滑动窗口聚合（7天/30天/90天）                              │
│ • 设备关联分析                                               │
│ • 行为序列特征提取                                           │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                Holo DWS/ADS 层（混合存储）                   │
├─────────────────────────────────────────────────────────────┤
│ • 用户特征宽表（行存）- 毫秒级点查                           │
│ • 时间窗口特征表（列存）- 历史分析                           │
│ • 关系图谱表（行存）- 多跳查询                               │
└────────────────────────┬────────────────────────────────────┘
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    应用层                                    │
├─────────────────────────────────────────────────────────────┤
│ 风控决策引擎 │ 实时评分卡 │ 欺诈检测 │ 监控大屏 │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、数据模型设计

### **1. ODS/DWD 层 - 明细数据（行存）**

#### **(1) 信贷申请明细表**

```sql
-- ============ 申请明细表（行存）============
CREATE TABLE dwd_credit_application (
    -- 主键
    application_id BIGINT PRIMARY KEY,
    
    -- 用户信息
    user_id BIGINT NOT NULL,
    id_card_no VARCHAR(18),
    mobile VARCHAR(11),
    
    -- 申请信息
    apply_amount DECIMAL(12,2),
    apply_term INT,              -- 申请期数
    product_code VARCHAR(50),
    channel VARCHAR(50),         -- 申请渠道
    
    -- 设备信息
    device_id VARCHAR(64),
    ip_address VARCHAR(50),
    gps_location VARCHAR(100),
    
    -- 状态信息
    status VARCHAR(20),          -- pending/approved/rejected/cancelled
    risk_level VARCHAR(20),      -- low/medium/high
    risk_score DECIMAL(5,2),
    
    -- 时间信息
    apply_time TIMESTAMP,
    update_time TIMESTAMP,
    dt VARCHAR(8),               -- 分区字段 yyyyMMdd
    
    -- 扩展字段
    extend_info JSONB            -- 存储动态字段
)
PARTITION BY LIST(dt)
WITH (
    orientation = 'row',                    -- ⭐ 行存：支持高频更新
    distribution_key = 'user_id',           -- 按用户分布
    shard_count = '128',                    -- 高并发写入
    binlog_level = 'replica',               -- 支持 Binlog 订阅
    time_to_live_in_seconds = '31536000'    -- 保留 1 年
);

-- 创建索引
CREATE INDEX idx_user_id ON dwd_credit_application(user_id, dt);
CREATE INDEX idx_device_id ON dwd_credit_application(device_id, dt);
CREATE INDEX idx_mobile ON dwd_credit_application(mobile, dt);
CREATE INDEX idx_apply_time ON dwd_credit_application(apply_time);
CREATE INDEX idx_status ON dwd_credit_application(status);

-- 创建分区（自动化脚本每天创建）
CREATE TABLE dwd_credit_application_20240115 
PARTITION OF dwd_credit_application 
FOR VALUES IN ('20240115');
```

#### **(2) 用户行为日志表**

```sql
-- ============ 用户行为日志（行存）============
CREATE TABLE dwd_user_behavior_log (
    log_id BIGINT,
    user_id BIGINT,
    application_id BIGINT,
    
    -- 行为信息
    event_type VARCHAR(50),      -- page_view/click/submit/verify
    event_detail VARCHAR(200),
    
    -- 设备信息
    device_id VARCHAR(64),
    ip_address VARCHAR(50),
    user_agent TEXT,
    
    -- 时间信息
    event_time TIMESTAMP,
    dt VARCHAR(8),
    hh VARCHAR(2),               -- 小时分区
    
    PRIMARY KEY (log_id, dt, hh)
)
PARTITION BY LIST(dt)
WITH (
    orientation = 'row',
    distribution_key = 'user_id',
    segment_key = 'hh',                     -- 按小时分段
    shard_count = '128',
    time_to_live_in_seconds = '2592000'     -- 保留 30 天
);

-- 按小时分区（细粒度分区）
CREATE TABLE dwd_user_behavior_log_20240115 
PARTITION OF dwd_user_behavior_log 
FOR VALUES IN ('20240115');
```

#### **(3) 设备指纹关系表**

```sql
-- ============ 设备关联关系（行存）============
CREATE TABLE dwd_device_relation (
    relation_id BIGINT PRIMARY KEY,
    
    -- 关系信息
    device_id VARCHAR(64) NOT NULL,
    user_id BIGINT,
    mobile VARCHAR(11),
    id_card_no VARCHAR(18),
    
    -- 关联强度
    relation_count INT,          -- 关联次数
    first_seen_time TIMESTAMP,
    last_seen_time TIMESTAMP,
    
    -- 风险标签
    is_black_device BOOLEAN,
    risk_tags VARCHAR(200),      -- 多头借贷,欺诈设备
    
    dt VARCHAR(8)
)
PARTITION BY LIST(dt)
WITH (
    orientation = 'row',
    distribution_key = 'device_id',
    shard_count = '64'
);

-- 设备关联查询索引
CREATE INDEX idx_device ON dwd_device_relation(device_id);
CREATE INDEX idx_user ON dwd_device_relation(user_id);
CREATE INDEX idx_mobile ON dwd_device_relation(mobile);
```

---

### **2. DWS 层 - 时间窗口特征（列存）**

#### **(1) 用户时间窗口特征表**

```sql
-- ============ 用户多时间窗口特征（列存）============
CREATE TABLE dws_user_feature_window (
    user_id BIGINT,
    stat_date DATE,              -- 统计日期
    window_type VARCHAR(10),     -- 7d/30d/90d/180d
    
    -- 申请行为特征
    apply_count INT,             -- 申请次数
    apply_amount_sum DECIMAL(18,2),
    apply_amount_avg DECIMAL(12,2),
    apply_amount_max DECIMAL(12,2),
    distinct_product_count INT,  -- 申请产品数
    distinct_channel_count INT,  -- 申请渠道数
    
    -- 审批结果特征
    approved_count INT,
    rejected_count INT,
    approval_rate DECIMAL(5,4),
    
    -- 设备行为特征
    distinct_device_count INT,   -- 使用设备数
    distinct_ip_count INT,       -- 使用IP数
    night_apply_count INT,       -- 夜间申请次数（22:00-06:00）
    
    -- 行为序列特征
    avg_apply_interval_hours DECIMAL(10,2),  -- 平均申请间隔
    max_apply_per_day INT,                   -- 单日最大申请数
    
    -- 风险特征
    high_risk_count INT,
    black_device_count INT,
    
    update_time TIMESTAMP,
    dt VARCHAR(8),
    
    PRIMARY KEY (user_id, stat_date, window_type, dt)
)
PARTITION BY LIST(dt)
WITH (
    orientation = 'column',                      -- ⭐ 列存：快速聚合
    distribution_key = 'user_id',
    segment_key = 'dt',
    clustering_key = 'dt,window_type,user_id',
    bitmap_columns = 'window_type',
    time_to_live_in_seconds = '15552000'         -- 保留 180 天
);

CREATE TABLE dws_user_feature_window_20240115 
PARTITION OF dws_user_feature_window 
FOR VALUES IN ('20240115');
```

**Flink 实时计算任务**：

```sql
-- Flink SQL: 计算用户 7 天滑动窗口特征
INSERT INTO dws_user_feature_window
SELECT 
    user_id,
    CURRENT_DATE as stat_date,
    '7d' as window_type,
    
    -- 申请行为
    COUNT(DISTINCT application_id) as apply_count,
    SUM(apply_amount) as apply_amount_sum,
    AVG(apply_amount) as apply_amount_avg,
    MAX(apply_amount) as apply_amount_max,
    COUNT(DISTINCT product_code) as distinct_product_count,
    COUNT(DISTINCT channel) as distinct_channel_count,
    
    -- 审批结果
    SUM(CASE WHEN status = 'approved' THEN 1 ELSE 0 END) as approved_count,
    SUM(CASE WHEN status = 'rejected' THEN 1 ELSE 0 END) as rejected_count,
    CAST(SUM(CASE WHEN status = 'approved' THEN 1 ELSE 0 END) AS DECIMAL) 
        / NULLIF(COUNT(*), 0) as approval_rate,
    
    -- 设备行为
    COUNT(DISTINCT device_id) as distinct_device_count,
    COUNT(DISTINCT ip_address) as distinct_ip_count,
    SUM(CASE WHEN HOUR(apply_time) >= 22 OR HOUR(apply_time) < 6 
        THEN 1 ELSE 0 END) as night_apply_count,
    
    -- 风险特征
    SUM(CASE WHEN risk_level = 'high' THEN 1 ELSE 0 END) as high_risk_count,
    
    CURRENT_TIMESTAMP as update_time,
    DATE_FORMAT(CURRENT_DATE, 'yyyyMMdd') as dt
    
FROM dwd_credit_application
WHERE dt >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, 7), 'yyyyMMdd')
GROUP BY user_id;

-- 类似地计算 30d, 90d, 180d 窗口
```

#### **(2) 设备多头特征表**

```sql
-- ============ 设备多头特征（列存）============
CREATE TABLE dws_device_multi_feature (
    device_id VARCHAR(64),
    stat_date DATE,
    window_type VARCHAR(10),
    
    -- 关联用户特征
    distinct_user_count INT,         -- 关联用户数 ⭐ 核心反欺诈特征
    distinct_mobile_count INT,       -- 关联手机号数
    distinct_idcard_count INT,       -- 关联身份证数
    
    -- 申请行为
    total_apply_count INT,
    total_apply_amount DECIMAL(18,2),
    distinct_product_count INT,
    
    -- 风险特征
    max_user_risk_score DECIMAL(5,2),
    high_risk_user_count INT,
    black_user_count INT,
    
    -- 时间特征
    first_apply_time TIMESTAMP,
    last_apply_time TIMESTAMP,
    apply_time_span_hours DECIMAL(10,2),
    
    update_time TIMESTAMP,
    dt VARCHAR(8),
    
    PRIMARY KEY (device_id, stat_date, window_type, dt)
)
PARTITION BY LIST(dt)
WITH (
    orientation = 'column',
    distribution_key = 'device_id',
    segment_key = 'dt',
    clustering_key = 'dt,window_type',
    bitmap_columns = 'window_type'
);
```

---

### **3. ADS 层 - 实时特征宽表（行存）**

#### **(1) 用户实时特征宽表**

```sql
-- ============ 用户实时特征宽表（行存）⭐⭐⭐ ============
-- 用于风控决策的毫秒级查询
CREATE TABLE ads_user_realtime_feature (
    user_id BIGINT PRIMARY KEY,
    
    -- ========== 基础信息 ==========
    id_card_no VARCHAR(18),
    mobile VARCHAR(11),
    register_time TIMESTAMP,
    
    -- ========== 最近申请信息 ==========
    last_apply_id BIGINT,
    last_apply_time TIMESTAMP,
    last_apply_amount DECIMAL(12,2),
    last_apply_status VARCHAR(20),
    last_device_id VARCHAR(64),
    last_ip_address VARCHAR(50),
    
    -- ========== 7天窗口特征 ==========
    apply_count_7d INT,
    apply_amount_sum_7d DECIMAL(18,2),
    approved_count_7d INT,
    rejected_count_7d INT,
    distinct_device_7d INT,
    distinct_ip_7d INT,
    night_apply_count_7d INT,
    
    -- ========== 30天窗口特征 ==========
    apply_count_30d INT,
    apply_amount_sum_30d DECIMAL(18,2),
    approval_rate_30d DECIMAL(5,4),
    avg_apply_interval_hours_30d DECIMAL(10,2),
    max_apply_per_day_30d INT,
    distinct_product_30d INT,
    
    -- ========== 90天窗口特征 ==========
    apply_count_90d INT,
    total_approved_amount_90d DECIMAL(18,2),
    
    -- ========== 设备风险特征 ==========
    current_device_user_count INT,       -- 当前设备关联用户数 ⭐
    current_device_risk_level VARCHAR(20),
    is_black_device BOOLEAN,
    
    -- ========== 行为序列特征 ==========
    behavior_pattern VARCHAR(200),       -- 行为模式标签
    last_10_apply_status VARCHAR(100),   -- 最近10次申请状态序列
    
    -- ========== 关系图谱特征 ==========
    related_high_risk_user_count INT,    -- 关联高风险用户数
    max_relation_depth INT,              -- 最大关联深度
    
    -- ========== 综合风险评分 ==========
    risk_score DECIMAL(5,2),             -- 实时风险评分
    risk_level VARCHAR(20),              -- low/medium/high/critical
    risk_tags VARCHAR(500),              -- 风险标签（JSON 数组）
    
    -- ========== 元数据 ==========
    feature_version VARCHAR(20),
    update_time TIMESTAMP,
    
    -- ========== 扩展字段 ==========
    extend_features JSONB                -- 动态特征
) WITH (
    orientation = 'row',                 -- ⭐ 行存：毫秒级点查
    distribution_key = 'user_id',
    shard_count = '128',
    binlog_level = 'replica'             -- 支持订阅变更
);

-- 核心索引
CREATE INDEX idx_mobile ON ads_user_realtime_feature(mobile);
CREATE INDEX idx_idcard ON ads_user_realtime_feature(id_card_no);
CREATE INDEX idx_risk_level ON ads_user_realtime_feature(risk_level);
CREATE INDEX idx_update_time ON ads_user_realtime_feature(update_time);
```

**特征更新策略**：

```sql
-- Flink 实时更新用户特征宽表
-- 方式1: 订阅明细表 Binlog，实时计算增量更新
CREATE TABLE holo_application_binlog (
    user_id BIGINT,
    application_id BIGINT,
    apply_amount DECIMAL(12,2),
    status VARCHAR(20),
    device_id VARCHAR(64),
    apply_time TIMESTAMP
) WITH (
    'connector' = 'hologres',
    'tablename' = 'dwd_credit_application',
    'binlog' = 'true'
);

-- 实时更新特征
INSERT INTO ads_user_realtime_feature
SELECT 
    user_id,
    -- 基础信息从维度表 JOIN
    ...
    -- 最近申请信息
    LAST_VALUE(application_id) as last_apply_id,
    LAST_VALUE(apply_time) as last_apply_time,
    ...
    -- 7天窗口特征（从 DWS 表 JOIN）
    f7.apply_count_7d,
    f7.apply_amount_sum_7d,
    ...
    CURRENT_TIMESTAMP as update_time
FROM holo_application_binlog a
LEFT JOIN dws_user_feature_window f7 
    ON a.user_id = f7.user_id 
    AND f7.window_type = '7d'
    AND f7.stat_date = CURRENT_DATE
...
```

#### **(2) 设备实时风险表**

```sql
-- ============ 设备实时风险表（行存）============
CREATE TABLE ads_device_realtime_risk (
    device_id VARCHAR(64) PRIMARY KEY,
    
    -- 关联信息
    related_user_count INT,          -- ⭐ 关联用户数（核心指标）
    related_mobile_count INT,
    related_idcard_count INT,
    
    -- 申请行为
    apply_count_7d INT,
    apply_count_30d INT,
    last_apply_time TIMESTAMP,
    
    -- 风险评估
    risk_score DECIMAL(5,2),
    risk_level VARCHAR(20),
    is_black_device BOOLEAN,
    risk_reason VARCHAR(500),
    
    -- 关联用户风险
    high_risk_user_count INT,
    max_user_risk_score DECIMAL(5,2),
    
    -- 行为异常
    same_hour_apply_count INT,       -- 同一小时内申请数
    cross_region_count INT,          -- 跨地域申请次数
    
    update_time TIMESTAMP
) WITH (
    orientation = 'row',
    distribution_key = 'device_id',
    shard_count = '64'
);
```

---

## 四、核心特征计算逻辑

### **1. 设备多头检测（图计算）**

```sql
-- ============ 设备关联用户数计算 ============
-- 方式1: Flink SQL 窗口聚合
INSERT INTO ads_device_realtime_risk
SELECT 
    device_id,
    COUNT(DISTINCT user_id) as related_user_count,
    COUNT(DISTINCT mobile) as related_mobile_count,
    COUNT(DISTINCT id_card_no) as related_idcard_count,
    COUNT(*) as apply_count_7d,
    MAX(apply_time) as last_apply_time,
    
    -- 风险评分计算
    CASE 
        WHEN COUNT(DISTINCT user_id) >= 10 THEN 90  -- 严重多头
        WHEN COUNT(DISTINCT user_id) >= 5 THEN 70   -- 中度多头
        WHEN COUNT(DISTINCT user_id) >= 3 THEN 50   -- 轻度多头
        ELSE 30
    END as risk_score,
    
    CASE 
        WHEN COUNT(DISTINCT user_id) >= 10 THEN 'critical'
        WHEN COUNT(DISTINCT user_id) >= 5 THEN 'high'
        WHEN COUNT(DISTINCT user_id) >= 3 THEN 'medium'
        ELSE 'low'
    END as risk_level,
    
    COUNT(DISTINCT user_id) >= 5 as is_black_device,
    
    CURRENT_TIMESTAMP as update_time
    
FROM dwd_credit_application
WHERE dt >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, 7), 'yyyyMMdd')
GROUP BY device_id;
```

### **2. 用户行为序列特征**

```sql
-- ============ 行为序列特征提取 ============
-- Flink SQL: 提取用户最近10次申请状态序列
SELECT 
    user_id,
    -- 使用 LISTAGG 聚合状态序列
    LISTAGG(status, ',') WITHIN GROUP (ORDER BY apply_time DESC) 
        OVER (PARTITION BY user_id ROWS BETWEEN CURRENT ROW AND 9 FOLLOWING) 
        as last_10_apply_status,
    
    -- 计算申请间隔
    AVG(TIMESTAMPDIFF(HOUR, 
        LAG(apply_time) OVER (PARTITION BY user_id ORDER BY apply_time),
        apply_time
    )) as avg_apply_interval_hours
    
FROM dwd_credit_application
WHERE dt >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, 30), 'yyyyMMdd');

-- 行为模式识别
-- 示例: rejected,rejected,rejected,pending -> 连续拒绝后再申请（高风险）
CASE 
    WHEN last_10_apply_status LIKE 'rejected,rejected,rejected%' 
        THEN 'continuous_rejection'
    WHEN apply_count_1d >= 5 
        THEN 'high_frequency'
    WHEN night_apply_count_7d >= 3 
        THEN 'night_apply'
    ELSE 'normal'
END as behavior_pattern
```

### **3. 关系图谱特征（多跳查询）**

```sql
-- ============ 关系图谱深度查询 ============
-- 查询与用户相关的二度关联高风险用户

-- 方式1: 递归 CTE（PostgreSQL 语法）
WITH RECURSIVE user_relation AS (
    -- 一度关联：共用设备的用户
    SELECT 
        a1.user_id as source_user,
        a2.user_id as related_user,
        1 as depth
    FROM dwd_credit_application a1
    JOIN dwd_credit_application a2 
        ON a1.device_id = a2.device_id 
        AND a1.user_id != a2.user_id
    WHERE a1.user_id = 123456  -- 目标用户
      AND a1.dt >= '20240108'
      AND a2.dt >= '20240108'
    
    UNION ALL
    
    -- 二度关联：关联用户的关联用户
    SELECT 
        r.source_user,
        a2.user_id as related_user,
        r.depth + 1
    FROM user_relation r
    JOIN dwd_credit_application a1 
        ON r.related_user = a1.user_id
    JOIN dwd_credit_application a2 
        ON a1.device_id = a2.device_id 
        AND a1.user_id != a2.user_id
    WHERE r.depth < 2  -- 限制深度
      AND a1.dt >= '20240108'
      AND a2.dt >= '20240108'
)
SELECT 
    source_user,
    COUNT(DISTINCT related_user) as related_user_count,
    MAX(depth) as max_relation_depth,
    -- JOIN 用户风险表统计高风险用户数
    SUM(CASE WHEN u.risk_level IN ('high', 'critical') THEN 1 ELSE 0 END) 
        as high_risk_user_count
FROM user_relation r
LEFT JOIN ads_user_realtime_feature u 
    ON r.related_user = u.user_id
GROUP BY source_user;
```

---

## 五、实时特征查询接口

### **1. 风控决策接口（毫秒级）**

```sql
-- ============ 单用户特征查询 ============
-- 应用场景：用户申请时实时调用

-- 查询接口（Java/Python 封装）
SELECT 
    user_id,
    
    -- 基础信息
    mobile,
    register_time,
    
    -- 申请行为
    apply_count_7d,
    apply_count_30d,
    approval_rate_30d,
    last_apply_time,
    
    -- 设备风险
    current_device_user_count,       -- ⭐ 核心：设备多头
    is_black_device,
    
    -- 行为异常
    night_apply_count_7d,
    max_apply_per_day_30d,
    
    -- 综合评分
    risk_score,
    risk_level,
    risk_tags
    
FROM ads_user_realtime_feature
WHERE user_id = ?;

-- 预期性能：< 5ms（行存 + 主键查询）
```

### **2. 设备风险查询**

```sql
-- ============ 设备风险实时查询 ============
SELECT 
    device_id,
    related_user_count,              -- ⭐ 关联用户数
    related_mobile_count,
    risk_score,
    risk_level,
    is_black_device,
    risk_reason,
    high_risk_user_count
FROM ads_device_realtime_risk
WHERE device_id = ?;

-- 预期性能：< 3ms
```

### **3. 批量特征查询（用于模型训练）**

```sql
-- ============ 批量导出训练样本 ============
SELECT 
    a.application_id,
    a.user_id,
    a.apply_amount,
    a.status as label,              -- 标签
    
    -- 用户特征
    f.apply_count_7d,
    f.apply_count_30d,
    f.approval_rate_30d,
    f.distinct_device_7d,
    f.night_apply_count_7d,
    f.avg_apply_interval_hours_30d,
    
    -- 设备特征
    d.related_user_count,
    d.risk_score as device_risk_score,
    
    -- 关系特征
    f.related_high_risk_user_count
    
FROM dwd_credit_application a
LEFT JOIN ads_user_realtime_feature f 
    ON a.user_id = f.user_id
LEFT JOIN ads_device_realtime_risk d 
    ON a.device_id = d.device_id
WHERE a.dt BETWEEN '20240101' AND '20240107'
  AND a.status IN ('approved', 'rejected');

-- 导出到 MaxCompute 用于模型训练
```

---

## 六、数据同步和更新策略

### **1. 实时数据流**

```sql
-- ============ Flink CDC 同步申请数据 ============
CREATE TABLE mysql_application (
    application_id BIGINT,
    user_id BIGINT,
    apply_amount DECIMAL(12,2),
    device_id VARCHAR(64),
    status VARCHAR(20),
    apply_time TIMESTAMP,
    PRIMARY KEY (application_id) NOT ENFORCED
) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'mysql.risk.com',
    'database-name' = 'credit_db',
    'table-name' = 'application'
);

-- 实时写入 Holo 明细表
INSERT INTO dwd_credit_application
SELECT 
    application_id,
    user_id,
    apply_amount,
    device_id,
    status,
    apply_time,
    DATE_FORMAT(apply_time, 'yyyyMMdd') as dt,
    CAST(NULL AS JSONB) as extend_info
FROM mysql_application;
```

### **2. 特征更新策略**

```sql
-- ============ 特征宽表更新（准实时）============
-- 策略1: 微批更新（每分钟）
INSERT INTO ads_user_realtime_feature
SELECT 
    user_id,
    ...
    -- 从窗口特征表 JOIN 最新数据
FROM (
    SELECT user_id 
    FROM dwd_credit_application 
    WHERE update_time >= NOW() - INTERVAL '1' MINUTE
) changed_users
LEFT JOIN dws_user_feature_window f7 
    ON changed_users.user_id = f7.user_id 
    AND f7.window_type = '7d'
...
ON CONFLICT (user_id) DO UPDATE SET
    last_apply_time = EXCLUDED.last_apply_time,
    apply_count_7d = EXCLUDED.apply_count_7d,
    ...
    update_time = CURRENT_TIMESTAMP;

-- 策略2: Binlog 触发更新（秒级）
-- Flink 订阅明细表变更，实时更新特征宽表
```

---

## 七、性能优化配置

### **1. 表级优化**

```sql
-- 行存表优化（高并发写入）
ALTER TABLE dwd_credit_application SET (
    autovacuum_enabled = true,
    autovacuum_vacuum_scale_factor = 0.05,  -- 更频繁的清理
    fillfactor = 90                         -- 预留更新空间
);

-- 列存表优化（查询性能）
ALTER TABLE dws_user_feature_window SET (
    compaction_strategy = 'auto',
    compaction_max_file_count = 50
);

-- 特征宽表优化（查询 + 更新）
ALTER TABLE ads_user_realtime_feature SET (
    autovacuum_vacuum_scale_factor = 0.1,
    toast_tuple_target = 8160              -- 优化 JSONB 字段
);
```

### **2. 查询优化**

```sql
-- 创建部分索引（减少索引大小）
CREATE INDEX idx_high_risk_user 
ON ads_user_realtime_feature(user_id) 
WHERE risk_level IN ('high', 'critical');

-- 创建表达式索引
CREATE INDEX idx_apply_date 
ON dwd_credit_application(DATE(apply_time));

-- 创建覆盖索引
CREATE INDEX idx_user_device_covering 
ON dwd_credit_application(user_id, device_id) 
INCLUDE (apply_time, status);
```

### **3. 分区管理**

```sql
-- 自动分区创建（定时任务）
DO $$
DECLARE
    partition_date DATE;
    partition_name TEXT;
BEGIN
    FOR i IN 0..7 LOOP  -- 提前创建未来 7 天分区
        partition_date := CURRENT_DATE + i;
        partition_name := 'dwd_credit_application_' || 
                         TO_CHAR(partition_date, 'YYYYMMDD');
        
        EXECUTE format('
            CREATE TABLE IF NOT EXISTS %I 
            PARTITION OF dwd_credit_application 
            FOR VALUES IN (%L)',
            partition_name,
            TO_CHAR(partition_date, 'YYYYMMDD')
        );
    END LOOP;
END $$;

-- 自动删除过期分区
DROP TABLE IF EXISTS dwd_credit_application_20231015;
```

---

## 八、监控和告警

### **1. 数据质量监控**

```sql
-- ============ 特征完整性监控 ============
SELECT 
    DATE(update_time) as stat_date,
    COUNT(*) as total_users,
    COUNT(CASE WHEN apply_count_7d IS NULL THEN 1 END) as missing_7d_feature,
    COUNT(CASE WHEN risk_score IS NULL THEN 1 END) as missing_risk_score,
    AVG(risk_score) as avg_risk_score,
    COUNT(CASE WHEN risk_level = 'critical' THEN 1 END) as critical_users
FROM ads_user_realtime_feature
WHERE update_time >= CURRENT_DATE
GROUP BY DATE(update_time);

-- 告警规则：
-- 1. missing_7d_feature / total_users > 0.05 -> 特征计算异常
-- 2. critical_users 激增 -> 风险事件
```

### **2. 性能监控**

```sql
-- 查询响应时间监控
SELECT 
    schemaname,
    tablename,
    seq_scan,                    -- 全表扫描次数
    idx_scan,                    -- 索引扫描次数
    n_tup_ins,                   -- 插入行数
    n_tup_upd,                   -- 更新行数
    n_live_tup,                  -- 活跃行数
    n_dead_tup,                  -- 死亡行数
    last_vacuum,
    last_autovacuum
FROM pg_stat_user_tables
WHERE tablename LIKE 'ads_%' OR tablename LIKE 'dwd_%'
ORDER BY n_dead_tup DESC;

-- 告警规则：
-- 1. n_dead_tup / n_live_tup > 0.2 -> 需要手动 VACUUM
-- 2. seq_scan > idx_scan -> 索引未生效
```

### **3. 实时数据延迟监控**

```sql
-- 监控特征更新延迟
SELECT 
    user_id,
    last_apply_time,
    update_time,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - update_time)) as delay_seconds
FROM ads_user_realtime_feature
WHERE update_time < CURRENT_TIMESTAMP - INTERVAL '5' MINUTE
ORDER BY delay_seconds DESC
LIMIT 100;

-- 告警规则：delay_seconds > 300 -> 特征更新延迟
```

---

## 九、总结

### **核心设计要点**

| 层级 | 表类型 | 存储格式 | 更新频率 | 查询模式 |
|------|--------|---------|---------|---------|
| **DWD 明细** | 申请/行为日志 | 行存 | 实时写入 | 点查、关联查询 |
| **DWS 特征** | 时间窗口特征 | 列存 | 分钟级 | 聚合分析 |
| **ADS 宽表** | 实时特征宽表 | 行存 | 秒级/分钟级 | 毫秒级点查 |

### **关键技术指标**

```
• 特征查询延迟：< 5ms (P99)
• 特征更新延迟：< 1 分钟
• 并发查询 QPS：> 10000
• 数据保留周期：明细 1 年，特征 180 天
• 特征维度：200+ 维度
```

### **风控决策流程**

```
用户申请 
  ↓
查询 ads_user_realtime_feature (用户特征)
  ↓
查询 ads_device_realtime_risk (设备风险)
  ↓
规则引擎判断
  ↓
返回决策结果 (通过/拒绝/人工审核)

总耗时：< 50ms
```
