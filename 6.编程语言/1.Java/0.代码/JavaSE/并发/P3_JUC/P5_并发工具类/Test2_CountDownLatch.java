package JavaSE.并发.P3_JUC.P5_并发工具类;

import java.util.concurrent.CountDownLatch;
/*
* 在这个例子中，我们首先创建一个CountDownLatch对象，并将它的计数器初始化为3（即需要等待3个线程执行完成）。
* 然后，我们创建3个线程，并将CountDownLatch对象传递给每个线程。每个线程在执行任务前，都会调用CountDownLatch的await()方法来等待其他线程执行完成。
* 当一个线程执行完任务后，会调用CountDownLatch的countDown()方法来减少计数器的值。当计数器的值减少到0时，所有线程都执行完成，主线程就可以执行下一步操作了。
* */
public class Test2_CountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            //这里创建线程就不使用匿名内部类，
            // 因为需要定义有参数的构造函数，并向构造函数中传递CountDownLatch对象
            //在run方法中调用CountDownLatch类的方法
            Thread thread = new Thread(new TaskRunnable(latch));
            thread.start();
        }

        // 等待所有线程执行完成
        latch.await();

        // 执行下一步操作
        System.out.println("All tasks have been finished.");
    }
}

class TaskRunnable implements Runnable {
    private final CountDownLatch latch;

    public TaskRunnable(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            // 模拟执行任务
            Thread.sleep((long) (Math.random() * 10000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 任务完成，调用CountDownLatch的countDown()方法
            latch.countDown();
        }
    }
}
