package JavaSE.并发.P3_JUC.P5_并发工具类;

public class Test1_ThreadLocalExample {

    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    static ThreadLocal<String> threadLocal1 = new ThreadLocal<>();

    static void print(String str){
        System.out.println(str + ":" + threadLocal.get() + threadLocal1.get());
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    threadLocal.set("abc");
                    threadLocal1.set("abcd");
                    print("thread1 variable");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // 及时清理当前线程的ThreadLocal变量，避免内存泄漏
                    threadLocal.remove();
                    threadLocal1.remove();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    threadLocal.set("def");
                    print("thread2 variable");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // 清理当前线程的ThreadLocal变量，避免内存泄漏
                    threadLocal.remove();
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
