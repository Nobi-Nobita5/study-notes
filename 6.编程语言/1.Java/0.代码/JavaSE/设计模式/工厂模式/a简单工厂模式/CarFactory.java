package JavaSE.设计模式.工厂模式.a简单工厂模式;

import JavaSE.设计模式.工厂模式.c抽象工厂模式.Car;

//创建核心工厂类，由他决定具体调用哪产品
public class CarFactory {
    public static Car createCar(String name) {
        if ("".equals(name)) {
            return null;
        }
        if(name.equals("奥迪")){
            return new AoDi();
        }
        if(name.equals("宝马")){
            return new Bmw();
        }
        return null;
    }
}
