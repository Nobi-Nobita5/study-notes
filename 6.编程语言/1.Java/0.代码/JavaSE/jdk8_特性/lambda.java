package JavaSE.jdk8_特性;

import org.junit.Test;


import java.util.ArrayList;

public class lambda {
    //lambda表达式代替匿名内部类
    @Test
    public void Runable() {

        //匿名内部类
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("The old runable now is using!");
            }
        }).start();

        //lambda表达式
        new Thread(()-> System.out.println("runable接口被实现啦")).start();
    }
    @Test
    public void runable() {
        new Thread(() -> System.out.println("It's a lambda function!")).start();
    }


    @Test
    public void test() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        //before Java.jdk8
        for (Integer x:
             list) {
            System.out.println(x);
        }
        //after Java.jdk8
        list.forEach(x-> System.out.println(x));
    }
}
