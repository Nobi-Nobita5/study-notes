package JavaSE.设计模式.代理模式.动态代理.P2_cglib动态代理;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;


public class CglibProxy implements MethodInterceptor {
    // 这里的目标类型为Object，则可以接受任意一种参数作为被代理类，实现了动态代理
    private Object targetObject;

    public CglibProxy(Object target) {
        this.targetObject = target;
    }
    //代理实际方法
    // proxy:代理对象 method:目标对象中的方法 args:目标对象中的方法 methodProxy:代理对象中的代理方法对象
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("开启事物");
        //使用method反射调用
        Object result = method.invoke(targetObject, args);
        System.out.println("关闭事物");
        // 返回代理对象
        return result;
    }
}
