#### 1.1、sqoop介绍

![在这里插入图片描述](https://img-blog.csdnimg.cn/1fb907a91b454cbe9cf34aefccc316b3.png#pic_center)

sqoop是apache旗下一款**“Hadoop和关系数据库服务器之间传送数据”**的工具。

导入数据：MySQL，Oracle导入数据到Hadoop的HDFS、HIVE、HBASE等数据存储系统；

**导出数据：**从Hadoop的文件系统中导出数据到关系数据库mysql等

将导入或导出命令翻译成mapreduce程序来实现

在翻译出的mapreduce中主要是对inputformat和outputformat进行定制

#### 1.2、sqoop常见问题

##### 1.2.1、sqoop中文数据乱码问题

- mysql导入到hdfs

  ~~~
  bin/sqoop import \
  --connect jdbc:mysql://node03:3306/A  \
  --username root \
  --password 123456 \
  --target-dir /A2 \
  --table B --m 1
  ~~~

  

- mysql导入到hive

  ~~~
  bin/sqoop import \
  --connect "jdbc:mysql://node03:3306/A?useUnicode=true&characterEncoding=utf-8" \
  --username root \
  --password 123456 \
  --table B \
  --hive-import \
  --m 1 \
  --hive-database default;
  ~~~

- 从hdfs导出到mysql

  ~~~
  bin/sqoop export \
  --connect "jdbc:mysql://node03:3306/A?useUnicode=true&characterEncoding=utf-8" \
  --username root \
  --password 123456 \
  --table B \
  --export-dir /user/hive/warehouse/b
  ~~~

##### 1.2.3、datax常见问题

1. oom

   ~~~
   在datax 中导数据使用过程中往往会因为，目标数据过大导致datax oom，那么可以调大datax的jvm参数来防止oom,在python命令后，使用 -jvm=”-Xms5G -Xmx 5G”来调大
   
   python datax.py  --jvm="-Xms5G -Xmx5G" ../job/test.json
   ~~~

2. 字段长度过长

   ~~~
   如果报java.io.IOException: Maximum column length of 100,000 exceeded in column...异常信息，说明数据源column字段长度超过了100000字符。
   需要在json的reader里增加如下配置
    "csvReaderConfig":{
    "safetySwitch": false,
     "skipEmptyRecords": false,
     "useTextQualifier": false
        }
      safetySwitch = false;//单列长度不限制100000字符
   ~~~

   