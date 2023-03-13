1. 乐观锁

1. 悲观锁

1. 什么是CAS？**（比较+交换+自旋）**

   > 1. CAS（CompareAndSwap），是一种**无锁编程的方法策略**。属于**乐观锁思想**。
   >
   > 2. 很多 synchronized 里面的代码只是一些很简单的代码，执行时间非常快，此时等待的线程都加锁可能是一种不太值得的操作，因为**线程阻塞涉及到用户态和内核态切换**的问题。既然synchronized 里面的代码执行得非常快，不妨让等待锁的线程不要被阻塞，而是在 synchronized的边界做忙循环，**这就是自旋**。如果做了**多次循环发现还没有获得锁，再阻塞**，这样可能是一种更好的策略。
   >
   > 3. **个人理解，尽量让线程【不要被阻塞】，从而减低锁带来的性能消耗，这也是锁升级的目的。**
   > 4. 注意：CAS必须要保证Compare和Swap这两个操作是原子性的，所幸各种不同架构的CPU都已经支持了原子性的CompareAndSwap。
   >
   > ![image-20230310095452020](http://springboot-vue-blog.oss-cn-hangzhou.aliyuncs.com/img-for-typora/image-20230310095452020.png?OSSAccessKeyId=LTAI5tCou6b1axdozAZhA4qP&Expires=9000000001&Signature=EIzkebxDyzDWkXgod00Fez1EDfs=)
   
1. 如java.util.concurrent.atomic中的AtomicInteger**原子类**的实现，就是用了CAS无锁编程的思想。