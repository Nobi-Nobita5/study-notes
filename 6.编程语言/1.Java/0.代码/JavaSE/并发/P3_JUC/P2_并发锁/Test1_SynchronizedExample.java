package JavaSE.并发.P3_JUC.P2_并发锁;

/**
 * @Author: Xionghx
 * @Date: 2023/05/29/20:43
 * @Version: 1.0
 */
public class Test1_SynchronizedExample {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized void decrement() {
        count--;
    }

    public synchronized int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        Test1_SynchronizedExample example = new Test1_SynchronizedExample();

        // 创建多个线程对共享变量进行操作
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    example.decrement();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                example.increment();
            }
        });

        // 启动两个线程
        t1.start();
        t2.start();

        // 主线程依次等待线程执行完毕，再执行后续内容
        t1.join();
        t2.join();

        // 输出结果
        System.out.println("Count: " + example.getCount());
    }
}
