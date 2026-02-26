

# Holo 建表完整指南

## 一、行存 vs 列存对比

### 1. **行存表 (Row-Oriented)**
```sql
-- 适用场景
- ✅ 高频点查、按主键查询
- ✅ 需要返回完整行数据
- ✅ 高并发写入（INSERT/UPDATE/DELETE）
- ✅ OLTP 场景

-- 示例
CREATE TABLE user_profile (
    user_id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    email VARCHAR(200)
) WITH (
    orientation='row'
);
```

### 2. **列存表 (Column-Oriented)**
```sql
-- 适用场景
- ✅ 大数据量聚合分析（SUM/AVG/COUNT）
- ✅ 只查询部分列
- ✅ 数据压缩率高
- ✅ OLAP 场景

-- 示例
CREATE TABLE order_analysis (
    order_id BIGINT,
    user_id BIGINT,
    amount DECIMAL(10,2),
    order_date DATE,
    PRIMARY KEY (order_id, order_date)
) WITH (
    orientation='column'
);

-- 典型查询
SELECT order_date, SUM(amount) 
FROM order_analysis 
WHERE order_date >= '2024-01-01'
GROUP BY order_date;
```

## 二、Holo 建表核心语法

### 1. **基础建表语法**
```sql
CREATE TABLE [IF NOT EXISTS] table_name (
    column1 datatype [PRIMARY KEY | NOT NULL],
    column2 datatype,
    ...
    [PRIMARY KEY (col1, col2)]  -- 复合主键
) 
[PARTITION BY LIST(column)]     -- 分区
WITH (
    -- 表属性
);
```

### 2. **数据类型**
```sql
-- 常用类型
BIGINT              -- 整数
DECIMAL(10,2)       -- 精确数值
VARCHAR(N)          -- 变长字符串
TEXT                -- 长文本
DATE                -- 日期
TIMESTAMP           -- 时间戳
BOOLEAN             -- 布尔
JSON/JSONB          -- JSON（JSONB 二进制存储更快）
ARRAY               -- 数组类型
```

### 3. **核心表属性 (WITH 子句)**

#### **存储格式**
```sql
WITH (
    orientation = 'row'     -- 行存（默认）
    -- 或
    orientation = 'column'  -- 列存
)
```

#### **分布键 (Distribution Key)**
```sql
-- 决定数据如何分布到各个 Shard
WITH (
    distribution_key = 'user_id',  -- 单列
    -- 或
    distribution_key = 'user_id,order_id'  -- 多列
)

-- 最佳实践
-- ✅ 选择高基数列（唯一值多）
-- ✅ 常用 JOIN 条件列
-- ✅ 避免数据倾斜
```

#### **Shard 数量**
```sql
WITH (
    shard_count = '20'  -- 建议：CPU 核数的 2-4 倍
)

-- 后期修改
CALL set_table_property('table_name', 'shard_count', '40');
```

#### **聚簇索引**
```sql
-- 行存表：主键自动创建聚簇索引
-- 列存表：可指定 clustering_key
WITH (
    orientation = 'column',
    clustering_key = 'order_date,user_id'  -- 排序键
)

-- 作用：加速范围查询和排序
SELECT * FROM orders 
WHERE order_date BETWEEN '2024-01-01' AND '2024-01-31'
ORDER BY order_date;
```

#### **生命周期 (TTL)**
```sql
WITH (
    time_to_live_in_seconds = '2592000'  -- 30天自动删除
)
```

#### **其他属性**
```sql
WITH (
    auto_vacuum = 'true',           -- 自动清理
    bitmap_columns = 'status,city', -- Bitmap 索引（列存） 可以给枚举列建bitmap索引
    dictionary_encoding_columns = 'category', -- 字典编码
    segment_key = 'dt'              -- 列存分段键
)
```

## 三、完整建表示例

### 示例 1：行存 OLTP 表
```sql
CREATE TABLE user_orders (
    order_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    create_time TIMESTAMP,
    update_time TIMESTAMP
) WITH (
    orientation = 'row',
    distribution_key = 'user_id',
    shard_count = '32'
);

-- 创建普通索引
CREATE INDEX idx_user ON user_orders(user_id);
CREATE INDEX idx_create_time ON user_orders(create_time);
```

### 示例 2：列存分区表
```sql
CREATE TABLE order_stats (
    order_id BIGINT,
    user_id BIGINT,
    amount DECIMAL(10,2),
    province VARCHAR(50),
    dt VARCHAR(8),  -- 分区字段
    PRIMARY KEY (order_id, dt)
)
PARTITION BY LIST(dt)  -- 按日期分区
WITH (
    orientation = 'column',
    distribution_key = 'user_id',
    clustering_key = 'dt,user_id',
    bitmap_columns = 'province',
    time_to_live_in_seconds = '7776000'  -- 90天
);

-- 创建分区
CREATE TABLE order_stats_20240101 PARTITION OF order_stats 
FOR VALUES IN ('20240101');
```

### 示例 3：行列共存（父子表）
```sql
-- 父表（列存，分析用）
CREATE TABLE orders_olap (
    order_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    amount DECIMAL(10,2),
    dt VARCHAR(8)
) WITH (
    orientation = 'column'
);

-- 子表（行存，点查用）
CREATE TABLE orders_oltp (
    LIKE orders_olap INCLUDING ALL
) WITH (
    orientation = 'row'
);

-- 数据同步（通过 Flink 或定时任务）
```

## 四、分区表详解

```sql
-- List 分区（离散值）
PARTITION BY LIST(region)

-- Range 分区（Holo 通过 List 模拟）
-- 需要手动创建每个分区
CREATE TABLE sales_202401 PARTITION OF sales FOR VALUES IN ('202401');
CREATE TABLE sales_202402 PARTITION OF sales FOR VALUES IN ('202402');

-- 查询自动分区裁剪
SELECT * FROM sales WHERE dt = '202401';  -- 只扫描对应分区
```

## 五、索引类型

```sql
-- 1. 主键索引（自动创建）
PRIMARY KEY (id)

-- 2. 普通 B-Tree 索引
CREATE INDEX idx_name ON table_name(column);

-- 3. Bitmap 索引（列存，低基数列）
WITH (bitmap_columns = 'gender,status')

-- 4. 表达式索引
CREATE INDEX idx_upper_name ON users(UPPER(name));
```

## 六、最佳实践建议

| 场景 | 推荐配置 |
|------|---------|
| **高频点查** | 行存 + 主键 + distribution_key |
| **大宽表分析** | 列存 + clustering_key + bitmap索引 |
| **实时写入** | 行存 + 合理 shard_count |
| **历史数据** | 列存分区表 + TTL |
| **JOIN 优化** | 两表 distribution_key 一致 |

```sql
-- 查看表属性
SELECT * FROM hologres.hg_table_properties WHERE table_name = 'your_table';

-- 查看表大小
SELECT pg_size_pretty(pg_total_relation_size('table_name'));
```


