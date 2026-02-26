## 问题

# 1、Holo 分区、分段、分布详解

## 一、PARTITION vs segment_key vs distribution_key

### 1. **物理存储架构图**

```
┌─────────────────── Holo 集群 ────────────────────┐
│                                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │
│  │  Worker 1   │  │  Worker 2   │  │ Worker 3 │ │  ← distribution_key
│  │  (物理节点)  │  │  (物理节点)  │  │ (物理节点)│ │     决定数据在哪个节点
│  └─────────────┘  └─────────────┘  └──────────┘ │
│         │                 │               │      │
│         ▼                 ▼               ▼      │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │
│  │ Partition 1 │  │ Partition 1 │  │Partition1│ │  ← PARTITION BY
│  │  dt=0101    │  │  dt=0101    │  │ dt=0101  │ │     逻辑分区（每个节点都有）
│  ├─────────────┤  ├─────────────┤  ├──────────┤ │
│  │ Partition 2 │  │ Partition 2 │  │Partition2│ │
│  │  dt=0102    │  │  dt=0102    │  │ dt=0102  │ │
│  └─────────────┘  └─────────────┘  └──────────┘ │
│         │                 │               │      │
│         ▼                 ▼               ▼      │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │
│  │ Segment 1   │  │ Segment 1   │  │Segment 1 │ │  ← segment_key
│  │  region=北京 │  │  region=上海 │  │region=深圳│ │     物理分段（列存优化）
│  ├─────────────┤  ├─────────────┤  ├──────────┤ │
│  │ Segment 2   │  │ Segment 2   │  │Segment 2 │ │
│  │  region=上海 │  │  region=北京 │  │region=北京│ │
│  └─────────────┘  └─────────────┘  └──────────┘ │
└───────────────────────────────────────────────────┘
```

### 2. **三者详细对比**

#### **(1) PARTITION BY - 逻辑分区**

```sql
CREATE TABLE orders (
    order_id BIGINT,
    user_id BIGINT,
    amount DECIMAL(10,2),
    dt VARCHAR(8),
    PRIMARY KEY (order_id, dt)
)
PARTITION BY LIST(dt);  -- ⭐ 逻辑分区

-- 手动创建分区
CREATE TABLE orders_20240101 PARTITION OF orders FOR VALUES IN ('20240101');
CREATE TABLE orders_20240102 PARTITION OF orders FOR VALUES IN ('20240102');
CREATE TABLE orders_20240103 PARTITION OF orders FOR VALUES IN ('20240103');
```

**物理存储特点**：

```
orders_20240101  -> 独立的物理表（有自己的文件）
orders_20240102  -> 独立的物理表
orders_20240103  -> 独立的物理表

每个分区：
- 独立的存储文件
- 独立的索引
- 独立的统计信息
- 可以单独备份/恢复/删除
```

**作用**：

```sql
-- 查询时自动分区裁剪
SELECT * FROM orders WHERE dt = '20240101';
-- 只扫描 orders_20240101 这一个物理表 ✅

-- 删除历史数据超快
DROP TABLE orders_20231201;  -- 秒级删除整个月的数据 ⚡

-- 独立管理
ALTER TABLE orders_20240101 SET (time_to_live_in_seconds = '86400');
```

#### **(2) segment_key - 物理分段（列存专用）**

```sql
CREATE TABLE orders (
    order_id BIGINT,
    user_id BIGINT,
    region VARCHAR(50),
    dt VARCHAR(8),
    PRIMARY KEY (order_id, dt)
) WITH (
    orientation = 'column',
    segment_key = 'region'  -- ⭐ 物理分段
);
```

**物理存储特点**：

```
单个表/分区内部，数据按 segment_key 分段存储：

orders 表的物理文件：
┌─ Segment: region=北京 ──────┐
│  Column: order_id [1,2,3]  │
│  Column: user_id [101,102]  │
│  Column: amount [100,200]   │
└────────────────────────────┘
┌─ Segment: region=上海 ──────┐
│  Column: order_id [4,5,6]  │
│  Column: user_id [103,104]  │
│  Column: amount [150,250]   │
└────────────────────────────┘

特点：
- 同一个物理文件内的逻辑分组
- 不是独立的表
- 主要用于查询优化（Segment 裁剪）
```

**作用**：

```sql
-- 查询时 Segment 裁剪
SELECT * FROM orders WHERE region = '北京';
-- 只扫描 region=北京 的 Segment，跳过其他 Segment ✅

-- 但不能像分区一样独立删除
-- ❌ 不能：DROP SEGMENT region='北京'
```

#### **(3) distribution_key - 数据分布（分布式存储）**

```sql
CREATE TABLE orders (
    order_id BIGINT,
    user_id BIGINT,
    amount DECIMAL(10,2),
    dt VARCHAR(8),
    PRIMARY KEY (order_id, dt)
) WITH (
    distribution_key = 'user_id',  -- ⭐ 按 user_id 哈希分布
    shard_count = '32'
);
```

**物理存储特点**：

```
数据通过哈希函数分布到不同的 Shard（物理存储单元）

hash(user_id) % 32 = shard_id

Shard 0:  user_id=100, 132, 164, ... (hash值 % 32 = 0)
Shard 1:  user_id=101, 133, 165, ... (hash值 % 32 = 1)
...
Shard 31: user_id=131, 163, 195, ... (hash值 % 32 = 31)

在分布式集群中：
Worker 1: 管理 Shard 0-10
Worker 2: 管理 Shard 11-21
Worker 3: 管理 Shard 22-31
```

**作用**：

```sql
-- 点查时直接定位到一个 Shard
SELECT * FROM orders WHERE user_id = 12345;
-- 计算 hash(12345) % 32 = 17
-- 只查询 Shard 17，不扫描其他 31 个 Shard ✅

-- JOIN 优化（两表 distribution_key 相同）
SELECT o.*, u.name
FROM orders o
JOIN users u ON o.user_id = u.user_id;
-- 本地 JOIN，无需跨节点数据传输 ⚡
```

---

### 3. **三者组合使用**

```sql
-- 完整示例：电商订单表
CREATE TABLE orders (
    order_id BIGINT,
    user_id BIGINT,
    region VARCHAR(50),
    status VARCHAR(20),
    amount DECIMAL(10,2),
    dt VARCHAR(8),
    PRIMARY KEY (order_id, dt)
)
PARTITION BY LIST(dt)  -- ⭐ 按日期逻辑分区
WITH (
    orientation = 'column',
    distribution_key = 'user_id',    -- ⭐ 按用户分布到不同节点
    segment_key = 'region',          -- ⭐ 分区内按地区分段
    clustering_key = 'region,status',
    bitmap_columns = 'region,status',
    shard_count = '32'
);

-- 创建分区
CREATE TABLE orders_20240101 PARTITION OF orders FOR VALUES IN ('20240101');
CREATE TABLE orders_20240102 PARTITION OF orders FOR VALUES IN ('20240102');
```

**物理存储结构**：

```
集群层面（distribution_key）：
├─ Worker 1
│  ├─ orders_20240101 (Shard 0-10)
│  │  ├─ Segment: region=北京
│  │  └─ Segment: region=上海
│  └─ orders_20240102 (Shard 0-10)
│
├─ Worker 2
│  ├─ orders_20240101 (Shard 11-21)
│  └─ orders_20240102 (Shard 11-21)
│
└─ Worker 3
   ├─ orders_20240101 (Shard 22-31)
   └─ orders_20240102 (Shard 22-31)
```

**查询优化效果**：

```sql
SELECT region, SUM(amount)
FROM orders
WHERE dt = '20240101'           -- ⭐ 分区裁剪：只扫描 orders_20240101
  AND user_id = 12345           -- ⭐ Shard 裁剪：只查 hash(12345) 对应的 Shard
  AND region = '北京'            -- ⭐ Segment 裁剪：只读 region=北京 的 Segment
GROUP BY region;

-- 优化路径：
-- 1. 365 个分区 -> 裁剪到 1 个分区
-- 2. 32 个 Shard -> 裁剪到 1 个 Shard
-- 3. N 个 Segment -> 裁剪到 1 个 Segment
-- 4. 所有列 -> 只读 region, amount 两列（列存）

-- 扫描数据量：从 100TB 降到 几MB ⚡⚡⚡
```

---

### 4. **对比总结表**

| 维度         | PARTITION BY      | segment_key          | distribution_key      |
| ------------ | ----------------- | -------------------- | --------------------- |
| **层级**     | 表级（最外层）    | 分区/表内（中间层）  | 集群级（底层）        |
| **物理存储** | 独立的子表文件    | 同一文件内的逻辑分组 | 跨节点的数据分片      |
| **主要作用** | 数据生命周期管理  | 列存查询优化         | 分布式并行            |
| **适用存储** | 行存 + 列存       | 仅列存               | 行存 + 列存           |
| **典型字段** | 时间字段（dt）    | 低基数字段（region） | 高基数字段（user_id） |
| **独立管理** | ✅ 可独立删除/备份 | ❌ 不可独立操作       | ❌ 自动管理            |
| **查询裁剪** | 分区裁剪          | Segment 裁剪         | Shard 裁剪            |
| **性能提升** | 10-100倍          | 2-10倍               | 5-50倍（分布式）      |

---





---



# 2、数据量对点查的影响？

主键PRIMARY KEY索引底层是B-Tree

主键点查的时间复杂度都是O(log N)

```
SELECT * FROM table WHERE pk = 123;

执行步骤：
1. 解析 SQL
2. 通过主键索引定位 (B-Tree 查找)
3. 读取数据页
4. 返回结果

-- 关键：步骤2和3的耗时与表大小关系不大
```



虽然都是点查，但数据量超大时，也会有差异，受以下几点影响

```
1、缓存命中率影响
-- 查看表的缓存命中率
SELECT 
    schemaname,
    tablename,
    heap_blks_read,      -- 磁盘读取
    heap_blks_hit,       -- 缓存命中
    heap_blks_hit::float / (heap_blks_hit + heap_blks_read) AS cache_hit_ratio
FROM pg_statio_user_tables
WHERE tablename = 'your_table';

-- 缓存命中率 > 99% 才算理想

2、索引页大小（非叶子节点常驻内存，非叶子节点可能要读磁盘），shard分布（最好确保查询的主键是分布键）

3、并发影响
低并发：
- 100条表：0.5ms
- 1亿条表：1ms

高并发（1000 QPS）：
- 100条表：2ms (资源竞争)
- 1亿条表：10ms (缓存换出 + 锁竞争)
```

​	