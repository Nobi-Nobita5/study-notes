package JavaSE.设计模式.工厂模式.c抽象工厂模式;

//创建一个总工厂，及实现类（由总工厂的实现类决定调用那个工厂的那个实例）
public interface TotalFactory {
    // 创建汽车
    Car createCar();
    // 创建发动机
    Engine createEngine();
}
