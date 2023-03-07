package JavaSE.Bingfa.P2_ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
* 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，【如果这个唯一的线程因
* 为异常结束，那么会有一个新的线程来替代它】，他必须保证前一项任务执行完毕才能执行后一项。
* 保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
* */
public class Test5_NewSingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService newSingleThreadExecutor =
                Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            newSingleThreadExecutor.execute(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "index:" + index);
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }
                }
            });
        }
        newSingleThreadExecutor.shutdown();
    }
}
