行内有指标配置平台（CISS-WEB），解决了指标需求沟通环节多，开发效率慢的问题。

功能：

* 配置特征、指标，供业务进行特征挖掘、指标定义
* 自动生成特征脚本、指标加工脚本
* 在指标验证环节，可以通过web端新增、执行发布计划，并在业务数据库中验证
* 在投产环节，开发只需要发布物料即可

对于离线指标，数仓开发人员只需要开发中间表（包含特征字段），通过配置即可自动化生成指标

---

**如果是实时指标，特征字段通过BINLOG或者RMB上报获得。在指标生成的过程中，需要调用特征工程平台RCS-RISK，进行spark streaming回溯。**

这句话的核心含义：

1. **不仅仅是**解决不可加性聚合问题
2. **更重要的是**构建完整的实时特征生产链路
3. Spark Streaming回溯的作用：
   - 计算滑动窗口的不可加性聚合
   - 冷启动时回溯历史数据
   - 补充历史特征用于实时特征计算
   - 保证特征的连续性和完整性

本次优化，是解决回溯时内存的压力问题。

---



### 一、 调整参数，解决内存溢出问题：

~~~
spark-submit \
--class cn.webank.cnc.feature.launcher.Launcher \
--conf "spark.task.maxFailures=1" \
--driver-java-options -Xss64M \
--files $files \
--master yarn \
--deploy-mode cluster \
--executor-cores 1 \
--num-executors 48 \
--driver-memory 4g \
--executor-memory 4g \
--name rcs_risk_task_$TRIGGER_BATCH_ID \
--queue $HD_QUEUE \
rcsrisk-ciss-feature-SNAPSHOT.jar -featureIds=$FEATURE_IDS -recallTaskId=$RECALL_BATCH_ID  -triggerTaskId=$TRIGGER_BATCH_ID -startDate=$START_DATE -endDate=$END_DATE -artifactId=$env -sourceType=$SOURCE
~~~



注：复习参数优化方法，**要点**：

1. **Memory Config**：讲清 Executor/Driver 堆与非堆内存、memoryOverhead 作用。
2. **Memory Management**：说明内存池分区（fraction）与 Off-heap。
3. **Parallelism**：控制 cores、实例数，以及动态分配。
4. **Shuffle & Serialization**：Shuffle 分区、压缩、Kryo 序列化。
5. **Extras**：广播 join、GC 调优、数据倾斜处理。

---

### 二、数据查询tidb，改为查询hive

存储进tidb用mapPartitions，是有性能问题的，需要先转换为RDD

所以改为hive存储

---

### 三、spark实现全局排序：

关键问题是历史留痕数据落库到hive的排序方式

原来：

是Java对象做排序。之后是收集到Driver端全局排序。然后将历史留痕数据落库hive。

再之后就是RCS根据业务在ciss-web配置的新特征规则进行加工。

优化：

按照index_value(统计维度)重新分区，sortWithinPartitions分区内排序，然后分布式全局排序repartitionAndSortWithinPartitions：

------

我采用了 **多阶段归并排序（External Merge Sort）** 的思想，并结合 Spark 的分布式能力，分阶段完成排序。

------

## ✅ 我的应对方案如下：

### 一、确保分区内排序稳定

- `sortWithinPartitions` 本身不会产生 Shuffle，能确保每个分区内部有序。
- 这一步只是第一阶段排序，降低了后续全局排序的压力。

------

### 二、进行分布式全局排序（推荐使用 `repartitionAndSortWithinPartitions`）

- 使用 `repartitionAndSortWithinPartitions` 替代手动的 `sort + coalesce`：

  - 它基于 **分区器RangePartitioner**，在**分区前**会采样数据（**找到边界值**），根据 key 范围进行划分，这一步涉及 `shuffle`。从而使得：
    - 所有分区内部有序
    - 所有分区之间**全局有序**

  > 这样可以实现全局排序的效果，但仍保持分布式计算，避免单点内存瓶颈。
  >
  > 例如样本为 `[70, 75, 85, 90, 95]`
  >
  > 构建分区边界，比如 2 个分区时，边界可能是：
  >
  > - 分区 0：score <= 85
  > - 分区 1：score > 85

------

### 三、如果你需要聚合成一个文件，处理方式如下：

#### 情况 1：只需要 TopN，**使用堆结构 + takeOrdered**

1）第一步，不使用repartitionAndSortWithinPartitions全量合并，使用mapPartitions分区内取TOP N：

2）第二部，使用 `takeOrdered(100,...)` 合并全局，它会收集少量数据到 Driver，（假设有 100 个分区，最多收集 100*100 条记录）。

#### 情况 2：必须写成一个全局有序文件（如导出需求）

- 使用 Hadoop 的 `saveAsHadoopFile`，配合 `TotalOrderPartitioner` 实现分布式全局排序输出。
- 或者将 `repartitionAndSortWithinPartitions` 后的数据：
  - 写入多个文件（保持有序）
  - 后续通过 HDFS 合并工具如 `hdfs dfs -getmerge`，或 MapReduce/Hive 外部工具合并

------

## ❗ 避免的错误方式：

- **collect** 到 Driver 再排序（内存爆炸，极其危险）
- 使用 `sortBy（）` + `coalesce(1)` 输出（全量 shuffle + 单节点执行，易 OOM）

注：

sortBy()底层有很多隐式封装，输出结果是原始对象，结构复杂；

`coalesce(numPartitions=1)` 函数用于输出文件，会将多个分区合并为一个分区。

------



