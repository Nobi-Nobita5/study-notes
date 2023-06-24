package JavaSE.jdk8_特性.Stream;

/**
 * @Author: Xionghx
 * @Date: 2023/06/13/12:17
 * @Version: 1.0
 */
import java.util.ArrayList;
import java.util.List;

class Student {
    private String name;
    private int age;
    private int score;

    public Student(String name, int age, int score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getScore() {
        return score;
    }
}

public class StreamExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 20, 85));
        students.add(new Student("Bob", 19, 75));
        students.add(new Student("Charlie", 18, 90));
        students.add(new Student("David", 22, 80));
        students.add(new Student("Eve", 21, 95));

        double averageScore = students.stream()
                .filter(student -> student.getAge() >= 18)  // 过滤得到年龄在18岁及以上的学生
                .mapToInt(Student::getScore)  // 获取学生的成绩
                .average()  // 计算平均值
                .orElse(0.0);  // 如果没有学生符合条件，则返回0.0

        System.out.println("平均成绩: " + averageScore);
    }
}
/**
 * 在上面的代码中，我们使用了流式API对学生列表进行处理。首先，我们使用stream()方法将学生列表转换为一个流。
 * 然后，我们使用filter()方法过滤出年龄在18岁及以上的学生。
 * 接着，我们使用mapToInt()方法将学生对象转换为对应的成绩值。最后，我们使用average()方法计算成绩的平均值。
 * 如果没有符合条件的学生，则使用orElse()方法设置默认值为0.0。
 */
