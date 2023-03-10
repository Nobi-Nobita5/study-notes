package JavaSE.SheJiMoShi.Single;
/*
线程安全的单例
    饿汉式：直接创建实例对象

    1.构造器私有化
    2.自行创建，用静态变量保存
    3.向外提供该实例
    3.强调是一个单例，可以用final修饰
 */
public class Singleton1 {
    public static final Singleton1 INSTANCE = new Singleton1();
    private Singleton1(){

    }
    public static void test(){

    }
}
