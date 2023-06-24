package JavaSE.并发.P3_JUC.P2_并发锁;

/**
 * @Author: Xionghx
 * @Date: 2023/06/14/18:20
 * @Version: 1.0
 * 问题：A B C线程如何指定顺序循环唤醒？
 */

/**
 * 方法一：
 * 这个程序中有三个方法threadA，threadB，和threadC，分别对应线程A，B，C的执行逻辑。我们使用一个共享变量flag来表示当前应该执行哪个线程。
 * 当一个线程在执行时，它会检查flag是否为它自己的标志，如果不是，它会调用wait()方法将自己挂起，并释放lock对象的锁。
 * 当它执行完毕后，它会修改flag的值，并调用notifyAll()方法，这将唤醒所有等待lock对象锁的线程。
 */
public class Test0_ThreadOrderDemo {
    private int flag = 1;
    private final Object lock = new Object();

    public void threadA() {
        synchronized (lock) {
            for (int i = 0; i < 10; i++) {
                while (flag != 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("A");
                flag = 2;
                lock.notifyAll();
            }
        }
    }

    public void threadB() {
        synchronized (lock) {
            for (int i = 0; i < 10; i++) {
                while (flag != 2) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("B");
                flag = 3;
                lock.notifyAll();
            }
        }
    }

    public void threadC() {
        synchronized (lock) {
            for (int i = 0; i < 10; i++) {
                while (flag != 3) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("C");
                flag = 1;
                lock.notifyAll();
            }
        }
    }

    /**
     * 在main方法中，我们创建了三个线程并启动它们。因为每个线程在开始时都会检查flag，
     * 所以即使它们可能会在任意顺序启动，但它们的执行顺序将总是A->B->C，并且会重复10次。
     */
    public static void main(String[] args) {
        Test0_ThreadOrderDemo threadOrderDemo = new Test0_ThreadOrderDemo();
        new Thread(() -> threadOrderDemo.threadA()).start();
        new Thread(() -> threadOrderDemo.threadB()).start();
        new Thread(() -> threadOrderDemo.threadC()).start();
    }

}

