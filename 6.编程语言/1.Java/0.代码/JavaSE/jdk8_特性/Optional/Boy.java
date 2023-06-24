package JavaSE.jdk8_特性.Optional;

/**
 * @Author: Xionghx
 * @Date: 2023/06/13/13:00
 * @Version: 1.0
 */
/**
 * @author shkstart
 * @create 2019 下午 7:22
 */
public class Boy {
    private Girl girl;

    @Override
    public String toString() {
        return "Boy{" +
                "girl=" + girl +
                '}';
    }

    public Girl getGirl() {
        return girl;
    }

    public void setGirl(Girl girl) {
        this.girl = girl;
    }

    public Boy() {

    }

    public Boy(Girl girl) {

        this.girl = girl;
    }
}
