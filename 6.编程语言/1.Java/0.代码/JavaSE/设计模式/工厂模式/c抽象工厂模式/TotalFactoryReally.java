package JavaSE.设计模式.工厂模式.c抽象工厂模式;

//总工厂实现类，由他决定调用哪个工厂的那个实例
public class TotalFactoryReally implements TotalFactory{
    @Override
    public Car createCar() {
        return new CarA();
    }

    @Override
    public Engine createEngine() {
        return new EngineA();
    }
}
