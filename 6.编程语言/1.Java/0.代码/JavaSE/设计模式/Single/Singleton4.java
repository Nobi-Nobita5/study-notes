package JavaSE.设计模式.Single;
/*
懒汉式，延迟创建对象
也叫：双重检验锁方式实现单例模式
 */
public class Singleton4 {
    //为什么使用 volatile 关键字修饰
    /*
    * 原因一：有序性
    * INSTANCE = new Singleton4()，这段代码其实是分为三步执行：
    * 1. 为 INSTANCE 分配内存空间
    * 2. 初始化 INSTANCE
    * 3. 将 INSTANCE 指向分配的内存地址
    * 例如：线程T1执行了1和3，此时T2线程调用 getInstance() 后发现 INSTANCE 不为空，因此直接通过最后一行代码返回 INSTANCE，但是此时的 INSTANCE 还没有被初始化
    *      所以通过使用 volatile 禁止 JVM 的指令重排，保证在多线程环境下也能正常运行。
    *
    * 原因二：可见性
    * 在当前的Java 内存模型（JMM）中，线程可以把变量保存本地内存（比如机器的寄存器）中，而不是直接在主存中进行读写。
    * 这就可能造成一个线程在主存中修改了一个变量的值，而另外一个线程还继续使用它在寄存器中的变量值的拷贝，
    * 而volatile关键字就可以保证修改的值会立即被更新到主内存中，当有其他线程需要读取时，它会去主内存中读取新值。
    * */
    private volatile static Singleton4 INSTANCE;
    private Singleton4(){

    }
    public static Singleton4 getINSTANCE(){
        //这里添加if判断的原因：
        //我们知道加锁是非常影响效率的(因为加锁会阻塞线程)，所以，如果 INSTANCE 已经不为null，就可以先判断对象是否已经实例过，再进入 synchronized 代码块
        if (INSTANCE==null) {
            //获取到该类class对象的锁，再执行里面的同步代码块
            synchronized (Singleton4.class) {
                /*
                这里添加if判断的原因：
                如果有两个线程t1、t2，都通过了getINSTANCE()方法的第一个if判断。
                然后t1获得class对象的锁，创建了INSTANCE实例，释放锁。
                然后等待中的t2线程获得class对象的锁，进入synchronized代码块，此时若没有下方的if判断，那t2线程也会创建一个实例。
                所以此处需要添加if判断，避免创建多个实例。
                */
                if (INSTANCE == null) {
                    INSTANCE = new Singleton4();
                }
            }
        }
        return INSTANCE;
    }
}
