package JavaSE.jdk8_特性.Optional;

/**
 * @Author: Xionghx
 * @Date: 2023/06/13/13:00
 * @Version: 1.0
 */
/**
 * @author shkstart
 * @create 2019 下午 7:23
 */
public class Girl {

    private String name;

    @Override
    public String toString() {
        return "Girl{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Girl() {

    }

    public Girl(String name) {

        this.name = name;
    }
}

