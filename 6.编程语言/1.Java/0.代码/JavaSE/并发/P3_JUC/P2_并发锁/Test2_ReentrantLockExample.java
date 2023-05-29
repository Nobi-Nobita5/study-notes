package JavaSE.并发.P3_JUC.P2_并发锁;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 1.调度更灵活，可中断、手动加锁释放锁
 * 2.支持公平锁和非公平锁
 * 3.可重入性
 */
public class Test2_ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock();
    public void doSomething() {
        lock.lock();
        try {
            // 这里是需要同步的代码块
        } finally {
            lock.unlock();
        }
    }
}
