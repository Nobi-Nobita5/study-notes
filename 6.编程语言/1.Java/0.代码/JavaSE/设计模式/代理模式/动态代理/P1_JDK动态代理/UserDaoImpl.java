package JavaSE.设计模式.代理模式.动态代理.P1_JDK动态代理;

public class UserDaoImpl implements UserDao{
    @Override
    public void save() {
        System.out.println("保存数据方法");
    }
}
