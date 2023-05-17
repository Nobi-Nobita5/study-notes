package JavaSE.设计模式.代理模式.动态代理.P2_cglib动态代理;

/**
 * cglib动态代理
 * 利用asm开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。
 * CGLIB动态代理和jdk代理一样，使用反射完成代理，不同的是他可以直接代理类。
 */
//测试CGLIB动态代理
public class Test {
    public static void main(String[] args) {
        CglibProxy cglibProxy = new CglibProxy();
        UserDao userDao = (UserDao) cglibProxy.getInstance(new UserDaoImpl());
        userDao.save();
    }
}
