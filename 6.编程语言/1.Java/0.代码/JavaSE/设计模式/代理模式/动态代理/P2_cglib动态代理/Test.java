package JavaSE.设计模式.代理模式.动态代理.P2_cglib动态代理;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * 案例源自《设计模式面试题》
 * cglib动态代理
 * 利用asm开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。
 * CGLIB动态代理和jdk代理一样，使用反射完成代理，不同的是他可以直接代理类。
 */
//测试CGLIB动态代理
public class Test {
    public static void main(String[] args) {
        // 创建空的字节码对象
        Enhancer enhancer = new Enhancer();
        // 设置字节码对象的父类也就是目标类
        //enhancer.setSuperclass(UserDaoImpl.class);
        //创建回调对象
        Callback callback = new CglibProxy(new UserDaoImpl());
        // 设置字节码对象的回调方法
        enhancer.setCallback(callback);
        // 得到代理对象
        UserDaoImpl cglibProxyDemo = (UserDaoImpl) enhancer.create();
        // 调用方法
        cglibProxyDemo.save();
    }
}
