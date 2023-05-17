package JavaSE.设计模式.工厂模式.a简单工厂模式;

import JavaSE.设计模式.工厂模式.c抽象工厂模式.Car;

public class Bmw implements Car {
    @Override
    public void run() {
        System.out.println("我是宝马汽车...");
    }
}
