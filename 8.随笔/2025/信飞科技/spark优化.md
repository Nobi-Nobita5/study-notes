问题：三方数据json解析遇到OOM，但是队列资源并没有打满，说明是单个executor内存不够（去重产生很多个executor）

DLI不支持任务级设置executor内存参数。

无法增加单个任务的内存的，用下面的方法：



-- 增加分区数，让每个任务处理更少数据

spark.sql.shuffle.partitions=1000;  -- 大幅增加分区
spark.sql.adaptive.enabled=false;  -- 关闭自适应，避免自动合并

spark.sql.adaptive.coalescePartitions.enabled=false;  --防止把分区合并得太大导致OOM

spark.sql.files.maxPartitionBytes=268435456; --增大maxPartitionBytes，减少文件读取的碎片化
