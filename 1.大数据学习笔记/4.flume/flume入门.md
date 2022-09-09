一、**Flume定义**

1. Flume是Cloudera提供的一个高可用的，高可靠的，[分布式](https://so.csdn.net/so/search?q=分布式&spm=1001.2101.3001.7020)的海量日志采集、聚合和传输的系统。Flume基于流式架构，灵活简单。

   *  Flume的主要作用就是，实时读取服务器本地磁盘的数据，将数据写入到HDFS

2. Flume的组成架构

   webServer==>Agent==>HDFS

![在这里插入图片描述](https://img-blog.csdnimg.cn/bf96abd667ed4fa4b7d87bb26989908c.png)

* Agent

  gent是一个[JVM](https://so.csdn.net/so/search?q=JVM&spm=1001.2101.3001.7020)进程，它以事件的形式将数据从源头送至目的，是Flume数据传输的基本单元。

  Agent主要有3个部分组成，Source、Channel、Sink。

  * **Source**
  	Source是负责接收数据到Flume Agent的组件。Source组件可以处理各种类型、各种格式的日志数据，包括avro、thrift、exec、		jms、spooling directory、netcat、sequence generator、syslog、http、legacy。
  	
  * **Channel**
  	Channel是位于Source和Sink之间的**缓冲区**。因此，Channel允许Source和Sink运作在不同的速率上。**Channel是线程安全的**，可以同时处理几个Source的写入操作和几个Sink的读取操作。
  	
  	Flume自带两种Channel：**Memory Channel**和**File Channel**。
  	
  	Memory Channel是内存中的队列。Memory Channel在不需要关心数据丢失的情景下适用。如果需要关心数据丢失，那么Memory  Channel就不应该使用，因为程序死亡、机器宕机或者重启都会导致数据丢失。
  	
  	File Channel将所有事件写到磁盘。因此在程序关闭或机器宕机的情况下不会丢失数据。
  	
  * **Sink**

    Sink不断地轮询Channel中的事件且批量地移除它们，并将这些事件批量写入到存储或索引系统、或者被发送到另一个Flume Agent。

    **Sink是完全事务性的**。在从Channel批量删除数据之前，每个Sink用Channel启动一个事务。批量事件一旦成功写出到存储系统或下一个Flume Agent，Sink就利用Channel提交事务。事务一旦被提交，该Channel从自己的内部缓冲区删除事件。

    Sink组件目的地包括hdfs、logger、avro、thrift、ipc、file、null、HBase、solr、自定义。

  * **Event**

    传输单元，Flume数据传输的基本单元，以事件的形式将数据从源头送至目的地。

3. **Flume拓扑结构**
4. **Flume Agent内部原理**
5. **单数据源、多数据源案例**
6. **Flume事务机制**
7. Flume的Channel Selectors
8. Flume参数调优

  