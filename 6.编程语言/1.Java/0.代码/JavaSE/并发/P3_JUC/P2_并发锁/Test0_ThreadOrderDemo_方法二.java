package JavaSE.并发.P3_JUC.P2_并发锁;

/**
 * @Author: Xionghx
 * @Date: 2023/06/14/18:32
 * @Version: 1.0
 */

/**
 * 这个程序的执行逻辑很直接：首先，我们创建了三个线程对象threadA，threadB，threadC，分别对应线程A，B，C的执行逻辑。
 * 我们在一个for循环中启动这三个线程，并在每次启动一个线程后，立即调用join()方法，这会让主线程等待这个新启动的线程结束。
 * 这样可以保证线程A，B，C的执行顺序，并且这个顺序会循环10次。
 *
 * 注意这种方法只适用于线程的启动顺序，而无法控制线程在执行过程中的行为，也就是说如果线程A在启动后并不立即结束，那么线程B和C可能仍然会在线程A结束之前开始执行。
 * 如果你需要更细粒度的控制，你可能需要使用其他同步机制，例如wait()/notify()或者Lock/Condition。
 */
public class Test0_ThreadOrderDemo_方法二 {
    public static void main(String[] args) throws InterruptedException {
        Runnable printA = () -> System.out.println("A");
        Runnable printB = () -> System.out.println("B");
        Runnable printC = () -> System.out.println("C");

        for (int i = 0; i < 10; i++) {
            /**
             * Java中的Thread对象代表一个执行线程。当你调用一个Thread对象的start()方法时，会启动一个新的系统线程，
             * 这个线程会执行你提供的Runnable对象的run()方法。当run()方法执行完毕，线程也就结束了。不能再次启动这个线程，即使它已经结束。
             * 如果想要循环执行某个任务，应该在每次循环中创建一个新的Thread对象。可以将创建和启动线程的代码放入循环中，如下所示：
             */
            Thread threadA = new Thread(printA);
            Thread threadB = new Thread(printB);
            Thread threadC = new Thread(printC);

            threadA.start();
            threadA.join();

            threadB.start();
            threadB.join();

            threadC.start();
            threadC.join();
        }
    }
}
