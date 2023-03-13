package JavaSE.并发.P3_JUC.P2_并发锁;

import java.util.concurrent.locks.ReentrantLock;

public class Test1_ReentrantLock {
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
