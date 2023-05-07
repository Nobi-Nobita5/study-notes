package JavaSE.并发.P2_线程池;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
特点：
    newCachedThreadPool创建一个可缓存线程池，如果当前线程池的长度超过了处理的需要
    时，它可以灵活的回收空闲的线程，【会在线程空闲一定时间后(默认60s)自动回收线程】，当需要增加时， 它可以灵活的添加新的线程，
    不会对池的长度作任何限制
缺点：
    他虽然可以无线的新建线程，但是容易造成堆外内存溢出，因为它的最大值是在初始化的时
    候设置为 Integer.MAX_VALUE，一般来说机器都没那么大内存给它不断使用。当然知道可能出问
    题的点，就可以去重写一个方法限制一下这个最大值
总结：
    线程池为无限大，【当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线
    程，而不用每次新建线程】。【由于有的任务需要创建新线程，有的可以直接复用线程。所以可能会出现后面的任务比前面的任务先执行完成的情况】。
* */
public class Test2_NewCachedThreadPool {
    public static void main(String[] args) {
        // 创建无限大小线程池，由jvm自动回收
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            newCachedThreadPool.submit(new Runnable() { //异步调用，run()方法作为execute()方法的参数。
                                                        // 不用等待run()执行完成，可以直接执行后面的循环。
                 // 匿名内部类的类体部分
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    System.out.println(Thread.currentThread().getName() +
                            ",任务编号i==" + temp);
                }
            });
        }
        newCachedThreadPool.shutdown();
    }
}
