package JavaSE.Bingfa.P2_ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
* newFixedThreadPool也可以复用线程。和newCachedThreadPool不同，
* newFixedThreadPool创建的线程池会预先创建一定数量的线程，
* 而且这些线程会一直存在于线程池中，直到线程池被关闭。
* 当任务到达线程池时，线程池中的线程会被分配执行任务，
* 执行完成后，这些线程会被重新利用，而不是被销毁。
*
* 特点：创建一个定长线程池，可控制最大并发数，超出的线程会在阻塞队列中等待。注意阻塞队列是无界的。所以线程池的大小最好根据系统资源进行设置。
* */
public class Test3_NewFixedThreadPool {
    public static void main(String[] args) {
        ExecutorService newFixedThreadPool =
                Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            newFixedThreadPool.execute(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() +
                        ",i==" + temp + "；核心数" + Runtime.getRuntime().availableProcessors());
                }
            });
        }
        newFixedThreadPool.shutdown();
    }
}
