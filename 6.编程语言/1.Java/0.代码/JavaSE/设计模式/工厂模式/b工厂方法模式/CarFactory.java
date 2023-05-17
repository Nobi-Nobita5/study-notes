package JavaSE.设计模式.工厂模式.b工厂方法模式;

//创建工厂方法调用接口（所有的产品需要new出来必须继承他来实现方法）
public interface CarFactory {
    Car createCar();
}
