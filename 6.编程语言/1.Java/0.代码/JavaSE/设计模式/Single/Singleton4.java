package JavaSE.设计模式.Single;
/*
懒汉式，延迟创建对象
也叫：双重检验锁方式实现单例模式
 */
public class Singleton4 {
    //采用 volatile 关键字修饰也是很有必要。
    /*INSTANCE = new Singleton4()，这段代码其实是分为三步执行：
    * 1. 为 INSTANCE 分配内存空间
    * 2. 初始化 INSTANCE
    * 3. 将 INSTANCE 指向分配的内存地址
    * 使用 volatile 可以禁止 JVM 的指令重排，保证在多线程环境下也能正常运行。
    * */
    private volatile static Singleton4 INSTANCE;
    private Singleton4(){

    }
    public static Singleton4 getINSTANCE(){
        /*每个线程进来，不管三七二十一，都要先进入同步代码块再说，如果说现在 INSTANCE 已经不为null了，那么，此时当一个线程进来，先获得锁，
        然后才会执行 if 判断。我们知道加锁是非常影响效率的，所以，如果 INSTANCE 已经不为null，是不是就可以先判断对象是否已经实例过，
        再进入 synchronized 代码块。如下*/
        if (INSTANCE==null) {
            //类对象加锁，获取到该类对象的锁，再执行里面的同步代码块
            synchronized (Singleton4.class) {
                /*如果把里面的 if 判断去掉，就相当于只对 INSTANCE = new Singleton4() 这一行代码加了个锁，
                只对一行代码加锁，那岂不是加了个寂寞（加锁的目的就是防止在第二个if判断和new操作之间有别的线程进来！！）*/
                if (INSTANCE == null) {
                    INSTANCE = new Singleton4();
                }
            }
        }
        return INSTANCE;
    }
    public void test(){

    }
}
