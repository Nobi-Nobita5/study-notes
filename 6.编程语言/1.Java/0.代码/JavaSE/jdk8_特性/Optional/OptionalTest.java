package JavaSE.jdk8_特性.Optional;

/**
 * @Author: Xionghx
 * @Date: 2023/06/13/13:00
 * @Version: 1.0
 */

import org.junit.Test;

import java.util.Optional;

/**
 * Optional类：为了在程序中避免出现空指针异常而创建的。
 *
 * 常用的方法：ofNullable(T t)
 *            orElse(T t)
 * @author lxy
 * @version 1.0
 * @Description
 * @date 2022/7/21 14:53
 */
public class OptionalTest {

    //传统的空指针异常以及解决办法
    @Test
    public void test1(){
        Boy boy = new Boy();
        boy = null;
        // String girlName = getGirlName(boy);
        String girlName = getGirlName1(boy);
        System.out.println(girlName);

    }
    public String getGirlName(Boy boy){
        return boy.getGirl().getName();
    }
    //优化以后的getGirlName():
    public String getGirlName1(Boy boy){
        if(boy != null){
            Girl girl = boy.getGirl();
            if(girl != null){
                return girl.getName();
            }
        }

        return null;
    }


    //使用Optional解决空指针异常
    @Test
    public void test2(){
        /*
          Optional.of(T t) : 创建一个 Optional 实例，t必须非空；
          Optional.empty() : 创建一个空的 Optional 实例
          Optional.ofNullable(T t)：t可以为null
        */
        Girl girl = new Girl();
        girl = null;  // NullPointerException
        //of(T t):保证t是非空的
        //Optional<Girl> optionalGirl = Optional.of(girl);
        //System.out.println(optionalGirl);
        //ofNullable(T t)：t可以为null
        Optional <Girl> optionalGirl1 = Optional.ofNullable(girl);
        System.out.println(optionalGirl1);
        //orElse(T t1):如果单前的Optional内部封装的t是非空的，则返回内部的t.
        //如果内部的t是空的，则返回orElse()方法中的参数t1.
        Girl girl1 = optionalGirl1.orElse(new Girl("赵丽颖"));
        System.out.println(girl1);
    }

    @Test
    public void test3(){
        Boy boy = null;
        boy = new Boy();
        boy = new Boy(new Girl("苍老师"));
        String girlName = getGirlName2(boy);
        System.out.println(girlName);
    }

    //使用Optional类的getGirlName()
    public String getGirlName2(Boy boy){
        Optional <Boy> boyOptional = Optional.ofNullable(boy);
        Boy boy1 = boyOptional.orElse(new Boy(new Girl("迪丽热巴")));
        Girl girl = boy1.getGirl();
        Optional <Girl> girlOptional = Optional.ofNullable(girl);
        Girl girl1 = girlOptional.orElse(new Girl("古丽扎纳"));
        return girl1.getName();
    }

}
