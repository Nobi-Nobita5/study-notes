java.util.concurrent.*包中的很多工具以及目前的一些主流的开源中间件都或多或少的使用了AQS。

**问题：**不是说CAS可以尽量避免线程阻塞吗，为啥AQS中还是**设计了等待队列**用来阻塞线程？

> 1. CAS仅针对单个变量的操作，不能用于多个变量来实现原子操作。在实现复杂的同步机制时，非阻塞算法可能无法满足要求，此时需要使用阻塞算法来保证同步的正确性。
> 2. 另外自旋等待可能在**某些情况**下比较高效（比如只有一行代码，而且锁竞争不激烈），但是对于**锁竞争较激烈的情况，自旋等待的效率会降低**。**因此，在实际的并发环境中，将线程放入等待队列中是更加有效和可靠的方法。**

#### 一、AQS是什么

AQS ( Abstract Queued Synchronizer ）是一个抽象的队列同步器，通过维护一个共享资源状态（ Volatile Int State ）和一个先进先出（ FIFO ）的线程等待队列来实现一个多线程访问共享资源的同步框架。

> AQS实现包括**排它锁（独占锁）和共享锁（读锁）**。
>
> 1. 排它锁是指同一时间只能有一个线程获得锁，其他线程必须等待。在AQS中，使用独占模式实现排它锁，通过实现tryAcquire和tryRelease方法来控制锁的获取和释放。
>
> 2. 共享锁是指多个线程可以同时获得锁，并行访问共享资源。在AQS中，使用共享模式实现共享锁，通过实现tryAcquireShared和tryReleaseShared方法来控制共享锁的获取和释放。
>
>    共享锁支持多个线程同时访问共享资源，但是并不是所有线程都可以同时获取锁。获取不到锁的线程依然会进入等待队列中等待被唤醒。
>
> 如在ReentrantReadWriteLock中读锁使用共享模式，写锁使用独占模式。
>
> 需要注意的是，**在使用AQS的时候，子类需要实现AQS中的tryAcquire(int arg)和tryRelease(int arg)方法**，这两个方法分别用于尝试获取和释放锁，acquire()方法和release()方法则基于这两个方法实现了获取和释放锁的逻辑。

思想：

> 1. **互斥变量（ Volatile Int State ）的设计，保证线程的安全性。**利用**CAS**，原子地修改**共享标记位**。
>
> 2. **等待队列。**未竞争到锁资源的线程在队列中等待；竞争到锁资源的线程释放锁之后从队列头部唤醒线程，再去竞争锁。
>
>    1）设计了一个先进先出（ FIFO ）的队列，让想获取共享资源的业务线程进行排队等待。
>
>    2）另外，不愿意等待的业务线程，也可以不进入等待队列，直接拿到返回值进行后面的业务操作。
>
> 3. **AQS支持锁竞争的公平性和非公平性**。
>
>    公平锁：队列中有阻塞的线程，就需要排队等待，被唤醒之后再去竞争锁；
>
>    ​				按照请求锁的顺序分配，拥有稳定获得锁的机会，但是性能可能比非公平锁低
>    
>    非公平锁：直接尝试更改互斥变量去获得锁。**没获取到锁再进入等待队列**。
>    
>    ​				不按照请求锁的顺序分配，不一定拥有获得锁的机会，但是性能可能比公平锁高。
>    
>    为什么公平锁的效率可能低于非公平锁的效率？
>    
>    ​		因为公平锁唤醒线程涉及操作系统用户态、内核态的切换，有短暂的延时，非公平锁可以利用这段时间，让线程直接尝试更改互斥变量去获得锁，来完成操作。所以在某些情况下，非公平锁性能会好些。

#### 二、AQS的作用

AQS 在 ReentrantLock、ReentrantReadWriteLock、Semaphore、CountDownLatch、ThreadPoolExcutor 的 Worker 中都有运用（JDK 1.8），AQS 是这些类的底层原理。

这些协作类，它们有很多工作是类似的，所以如果能把实现类似工作的代码给提取出来，变成一个新的底层工具类（或称为框架）的话，就可以直接使用这个工具类来构建上层代码了，而这个工具类其实就是 AQS。

#### 三、源码分析

1. tryAquire：

   * 需要实现AQS时，进行重写。

   * 这种开放式的设计，可以支持上层类的多样化实现：

     **1）比如可以重写tryAquire，使得线程没有获取到锁，直接拿到返回值，去执行后面的逻辑。**

     **2）重写tryAquire，实现公平锁，非公平锁的逻辑。ReentrantLock就是这样做的（详情见NonfairSync、FairSync两个内部类）。**

2. aquire：

   ~~~java
   public final void acquire(int arg) {
           if (!tryAcquire(arg) &&
               acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
               selfInterrupt();
       }
   ~~~

   1）先使用tryAcquire直接尝试修改互斥变量去获取锁；

   2）获取失败后再acquireQueued中的shouldParkAfterFailedAcquire判断当前线程是否要加入等待队列，并进行相应操作。

   ~~~java
       final boolean acquireQueued(final Node node, int arg) {
           boolean failed = true;
           try {
               boolean interrupted = false;
               for (;;) {
                   final Node p = node.predecessor();
                   if (p == head && tryAcquire(arg)) {
                       setHead(node);
                       p.next = null; // help GC
                       failed = false;
                       return interrupted;
                   }
                   if (shouldParkAfterFailedAcquire(p, node) &&
                       parkAndCheckInterrupt())
                       interrupted = true;
               }
           } finally {
               if (failed)
                   cancelAcquire(node);
           }
       }
   ~~~

   这里的parkAndCheckInterrupt()方法中返回了interrupted()中断状态，为什么在挂起线程后，要返回这个中断状态？

   > 这是因为用LockSupport.park()方法挂起在等待队列中的线程，无法响应外部的中断请求，只有当线程获取锁之后，然后再进行中断响应。
   >
   > 所以要返回中断状态，在线程执行完acquireQueued()方法被唤醒后，获得了锁，再执行acquire()方法中的selfInterrupt()，使用Thread.currentThread().interrupt()来中断当前线程。

   3）

   * AbstractQueuedSynchronizer类中的private volatile int **state变量**，就是关键的**共享标记位**；

   * AbstractQueuedSynchronizer类中的Node内部类用来封装当前线程，Node中还有一些枚举变量用来标识当前线程的状态，如waitStatus等。
