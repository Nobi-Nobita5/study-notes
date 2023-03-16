import JavaSE.设计模式.Single.Singleton4;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Singleton4 instance = Singleton4.getINSTANCE();
                    System.out.println("通过getINSTANCE得到了对象："+ instance);
                    Thread.sleep(10000);
                    System.out.println("线程1正在运行:"+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("主线程1正在运行:"+ Thread.currentThread().getName());
        Class<Singleton4> singleton4Class = Singleton4.class;
        Constructor<Singleton4> constructor = singleton4Class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Singleton4 singleton4 = constructor.newInstance();
        System.out.println("通过反射得到了对象:"+singleton4);
    }

}
