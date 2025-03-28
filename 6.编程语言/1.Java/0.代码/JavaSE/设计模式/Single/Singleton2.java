package JavaSE.设计模式.Single;
/*
线程安全的单例
    饿汉式：直接创建实例对象

    1.构造器私有化
    2.自行创建，用静态变量保存
    3.向外提供该实例
    3.强调是一个单例，可以用final修饰
 */
public class Singleton2 {
    public static final Singleton2 INSTANCE;
    static {
        INSTANCE = new Singleton2();
    }
    private Singleton2(){

    }
    public static void test(){

    }
}
