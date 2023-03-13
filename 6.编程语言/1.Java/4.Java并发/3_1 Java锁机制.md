

解答顺序：

1. 什么是锁

​		由于JVM内存结构，堆和方法区是共享的，多线程操作共享变量，需要保证线程安全。就出现了锁机制。

2. 对象内存结构

3. Synchronized原理

   > 1. Synchronized编译后，底层是由monitor管控，实现线程串行操作共享变量。（monitor enter和monitor exit指令）
   > 2. 可以通过wait()释放锁、挂起线程，notify()获取锁、唤醒线程。
   > 3. 而**monitor是依赖操作系统提供的【底层同步原语】mutex lock来实现的**。java线程实际上是对操作系统线程的映射，所以挂起和唤醒线程都**需要切换内核态与用户态，**会对程序性能产生严重的影响。
   
4. Java6对Synchronized进行了优化，引入了**锁升级**的机制。无锁，偏向锁、轻量级锁和重量级锁。

4. 除了Synchronized关键字，JUC并发包中也提供了如ReentrantLock、ReentrantReadWriteLock等并发锁。