package JavaSE.设计模式.代理模式.静态代理;

/**
 * 案例源自《设计模式面试题》
 * 静态代理
 * 缺点：每个需要代理的对象都需要自己重复编写代理，
 * 优点：但是可以面相实际对象或者是接口的方式实现代理
 */
public class Test {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        UserDaoProxy userDaoProxy = new UserDaoProxy(userDao);
        userDaoProxy.save();
    }
}
