package JavaSE.设计模式.工厂模式.b工厂方法模式;

public class BmwFactory implements CarFactory{
    @Override
    public Car createCar() {
        return new Bmw();
    }
}
