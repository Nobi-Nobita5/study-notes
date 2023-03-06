package Java.JavaSE.Bingfa.P1_ChuangJianFangShi;

import java.util.concurrent.Callable;

public class ThreadCreate {
    public static void main(String[] args) throws Exception {
        //main
        System.out.println("main:" + Thread.currentThread().getName());

        //Thread1
        Thread1 thread1 = new Thread1();
        Thread2 thread2 = new Thread2();
        Thread3 thread3 = new Thread3();
        thread1.run();
        thread2.run();
        thread3.call();
    }
}

class Thread1 extends Thread {
    @Override
    public void run() {
        System.out.println("实现Runnable,运行【创建的新线程】的当前线程是：" + Thread.currentThread().getName() + "。【创建的新线程】是" + this.getName());
    }
}

class Thread2 implements Runnable {

    @Override
    public void run() {
        System.out.println("实现Runnable：" + Thread.currentThread().getName());
    }
}

class Thread3 implements Callable {
    @Override
    public Object call() throws Exception {
        System.out.println("实现Callable: " + Thread.currentThread().getName());
        return Thread.currentThread().getName();
    }
}
