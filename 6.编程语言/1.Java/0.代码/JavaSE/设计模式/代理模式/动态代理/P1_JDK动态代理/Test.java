package JavaSE.设计模式.代理模式.动态代理.P1_JDK动态代理;

import java.lang.reflect.Proxy;

/**
 * JDK动态代理（接口代理，根据被代理的接口来动态生成代理类的class文件，并加载运行的过程）
 * 缺点：必须是面向接口，目标业务类必须实现接口
 * 优点：不用关心代理类，只需要在运行阶段才指定代理哪一个对象
 */
//利用动态代理使用代理方法
public class Test {
    public static void main(String[] args) {
        // 被代理对象
        UserDao userDaoImpl = new UserDaoImpl();
        InvocationHandlerImpl invocationHandlerImpl = new InvocationHandlerImpl(userDaoImpl);
        //类加载器
        ClassLoader loader = userDaoImpl.getClass().getClassLoader();
        //接口
        Class<?>[] interfaces = userDaoImpl.getClass().getInterfaces();
        // 主要装载器、一组接口及调用处理动态代理实例
        UserDao newProxyInstance = (UserDao) Proxy.newProxyInstance(loader, interfaces, invocationHandlerImpl);
        newProxyInstance.save();
    }
}
