package JavaSE.设计模式.代理模式.静态代理;
//代理类
public class UserDaoProxy extends UserDao{
    private UserDao userDao;

    public UserDaoProxy(UserDao userDao){
        this.userDao = userDao;
    }

    public void save(){
        System.out.println("开启事务");
        userDao.save();
        System.out.println("关闭事务");
    }
}
