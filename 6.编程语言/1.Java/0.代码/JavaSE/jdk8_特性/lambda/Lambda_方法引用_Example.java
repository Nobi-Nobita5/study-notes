package JavaSE.jdk8_特性.lambda;

/**
 * @Author: Xionghx
 * @Date: 2023/06/13/12:26
 * @Version: 1.0
 */
import java.util.ArrayList;
import java.util.List;

/**
 * 当要传递给Lambda体的操作，已经有实现的方法了，可以使用方法引用。
 * 在Java中，::是方法引用运算符，用于引用已经存在的方法或构造函数。它可以简化Lambda表达式的编写，使代码更加简洁和可读。
 *
 * 方法引用的语法是：
 * 类名/对象::方法名
 */
public class Lambda_方法引用_Example {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");

        // 使用Lambda表达式
        names.stream()
                .map(str -> str.toUpperCase())  // 调用字符串的 toUpperCase() 方法
                .forEach(System.out::println);

        System.out.println("------------------------");

        // 使用方法引用
        names.stream()
                .map(String::toUpperCase)  // 使用方法引用调用字符串的 toUpperCase() 方法
                .forEach(System.out::println);
    }
}
