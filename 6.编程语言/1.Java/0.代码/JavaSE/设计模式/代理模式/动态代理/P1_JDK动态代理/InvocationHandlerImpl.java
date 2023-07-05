package JavaSE.设计模式.代理模式.动态代理.P1_JDK动态代理;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

//下面是代理类，可重复使用，不像静态代理那样要自己重复编写代理
public class InvocationHandlerImpl implements InvocationHandler {

    private Object target;

    public InvocationHandlerImpl(Object target){
        this.target = target;
    }

    //动态代理实际运行的代理方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("调用开始处理");
        //使用method反射调用创建对象
        //第一个参数是要创建的对象，第二个是构成方法的参数，由第二个参数来决定创建对象使用哪个构造方法
        Object result = method.invoke(target, args);
        System.out.println("调用结束处理");
        return result;
    }
}
