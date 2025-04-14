"""
Python学习程序 - 面向Java开发者
"""

import sys
from enum import Enum, auto

class Lesson(Enum):
    VARIABLES = auto()
    DATA_TYPES = auto()
    OPERATORS = auto()
    CONTROL_FLOW = auto()
    FUNCTIONS = auto()
    CLASSES = auto()
    EXCEPTIONS = auto()
    COLLECTIONS = auto()
    COMPREHENSIONS = auto()
    LAMBDA = auto()
    FILES = auto()
    MODULES = auto()
    EXIT = auto()

def print_comparison(java_code, python_code):
    """显示Java和Python代码的对比"""
    print("\n🟨 Java 代码:")
    print(java_code)
    print("\n🟦 Python 代码:")
    print(python_code)
    print("\n" + "="*50)

def lesson_variables():
    """变量和基本输入输出"""
    print("\n📚 变量和基本输入输出")

    # Java对比
    java_code = """
// Java变量声明
int age = 25;
double height = 1.75;
boolean isStudent = true;
String name = "张三";

// 输出
System.out.println("姓名: " + name);
System.out.printf("年龄: %d, 身高: %.2f%n", age, height);
"""

    python_code = """
# Python变量声明
age = 25
height = 1.75
is_student = True
name = "张三"

# 输出
print(f"姓名: {name}")
print(f"年龄: {age}, 身高: {height:.2f}")

# 输入
user_input = input("请输入你的年龄: ")
print(f"你输入的年龄是: {user_input}")
"""

    print_comparison(java_code, python_code)

    # 交互练习
    print("🛠️ 现在让我们练习一下:")
    try:
        user_name = input("请输入你的名字: ")
        user_age = int(input("请输入你的年龄: "))
        print(f"你好, {user_name}! 明年你就{user_age + 1}岁了。")
    except ValueError:
        print("请输入有效的年龄数字!")

def lesson_data_types():
    """数据类型"""
    print("\n📚 数据类型")

    java_code = """
// Java数据类型
int num = 10;
double decimal = 3.14;
char letter = 'A';
boolean flag = false;
String text = "Hello";

// 数组
int[] numbers = {1, 2, 3};
String[] names = new String[3];
"""

    python_code = """
# Python数据类型
num = 10          # 整数 (不需要声明类型)
decimal = 3.14    # 浮点数
letter = 'A'      # 字符 (实际上也是字符串)
flag = False      # 布尔值 (首字母大写)
text = "Hello"    # 字符串

# Python是动态类型语言，可以重新赋值为不同类型
num = "现在是一个字符串"

# 列表 (类似Java的数组但更灵活)
numbers = [1, 2, 3]
names = ["Alice", "Bob", "Charlie"]

# 元组 (不可变)
coordinates = (10.0, 20.0)

# 集合 (不重复元素)
unique_numbers = {1, 2, 2, 3}  # 结果为 {1, 2, 3}

# 字典 (键值对)
person = {"name": "Alice", "age": 25}
"""

    print_comparison(java_code, python_code)

    # 类型演示
    print("\n🔍 类型演示:")
    print(f"numbers 的类型: {type([1, 2, 3])}")
    print(f"decimal 的类型: {type(3.14)}")
    print(f"flag 的类型: {type(False)}")

def lesson_operators():
    """运算符"""
    print("\n📚 运算符")

    java_code = """
// Java运算符
int a = 10, b = 3;

// 算术运算
int sum = a + b;
int quotient = a / b;  // 结果为3
double precise_quotient = (double)a / b;

// 比较运算
boolean isEqual = (a == b);

// 逻辑运算
boolean result = (a > 5) && (b < 5);

// 三元运算符
String message = (a > b) ? "a更大" : "b更大";
"""

    python_code = """
# Python运算符
a, b = 10, 3

# 算术运算
sum = a + b
quotient = a / b  # 结果为3.333... (与Java不同)
floor_quotient = a // b  # 地板除，结果为3

# 指数运算
power = a ** b  # 10的3次方

# 比较运算
is_equal = (a == b)

# 逻辑运算
result = (a > 5) and (b < 5)

# 三元表达式
message = "a更大" if a > b else "b更大"
"""

    print_comparison(java_code, python_code)

    # 运算符演示
    print("\n🔍 运算符演示:")
    print(f"10 / 3 = {10 / 3} (浮点除法)")
    print(f"10 // 3 = {10 // 3} (地板除)")
    print(f"10 ** 3 = {10 ** 3} (指数运算)")

def lesson_control_flow():
    """控制流"""
    print("\n📚 控制流")

    java_code = """
// Java控制流

// if-else
int score = 85;
if (score >= 90) {
    System.out.println("优秀");
} else if (score >= 60) {
    System.out.println("及格");
} else {
    System.out.println("不及格");
}

// switch
int day = 3;
switch (day) {
    case 1:
        System.out.println("周一");
        break;
    case 2:
        System.out.println("周二");
        break;
    default:
        System.out.println("其他");
}

// for循环
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}

// while循环
int count = 0;
while (count < 5) {
    System.out.println(count);
    count++;
}
"""

    python_code = """
# Python控制流

# if-elif-else
score = 85
if score >= 90:
    print("优秀")
elif score >= 60:
    print("及格")
else:
    print("不及格")

# match-case (Python 3.10+)
day = 3
match day:
    case 1:
        print("周一")
    case 2:
        print("周二")
    case _:
        print("其他")

# for循环 (类似Java的for-each)
for i in range(5):  # range(5)生成0-4
    print(i)

# while循环
count = 0
while count < 5:
    print(count)
    count += 1  # Python没有++运算符
"""

    print_comparison(java_code, python_code)

    # 控制流演示
    print("\n🔍 控制流演示:")
    print("for循环输出:")
    for i in range(3):
        print(f"循环次数: {i}")

    print("\n列表遍历:")
    fruits = ["苹果", "香蕉", "橙子"]
    for i, fruit in enumerate(fruits):
        print(f"{i+1}. {fruit}")

def lesson_functions():
    """函数"""
    print("\n📚 函数")

    java_code = """
// Java函数
public static int add(int a, int b) {
    return a + b;
}

// 方法重载
public static double add(double a, double b) {
    return a + b;
}

// 可变参数
public static int sum(int... numbers) {
    int total = 0;
    for (int num : numbers) {
        total += num;
    }
    return total;
}
"""

    python_code = """
# Python函数
def add(a, b):
    return a + b

# Python不需要重载，参数类型灵活
print(add(2, 3))      # 5
print(add(2.5, 3.7))  # 6.2

# 默认参数
def greet(name, message="你好"):
    return f"{message}, {name}!"

# 可变参数
def sum(*numbers):
    total = 0
    for num in numbers:
        total += num
    return total

# 关键字参数
def create_person(name, age, **kwargs):
    person = {"name": name, "age": age}
    person.update(kwargs)
    return person

# 返回多个值
def get_min_max(numbers):
    return min(numbers), max(numbers)
"""

    print_comparison(java_code, python_code)

    # 函数演示
    print("\n🔍 函数演示:")
    print(f"add(2, 3) = {add(2, 3)}")
    print(f"greet('Alice') = {greet('Alice')}")
    print(f"sum(1, 2, 3, 4) = {sum(1, 2, 3, 4)}")
    min_val, max_val = get_min_max([5, 2, 8, 1])
    print(f"最小值: {min_val}, 最大值: {max_val}")

def add(a, b):
    return a + b

def greet(name, message="你好"):
    return f"{message}, {name}!"

def sum(*numbers):
    total = 0
    for num in numbers:
        total += num
    return total

def get_min_max(numbers):
    return min(numbers), max(numbers)

def lesson_classes():
    """类和面向对象"""
    print("\n📚 类和面向对象")

    java_code = """
// Java类
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void greet() {
        System.out.println("你好, 我是 " + name);
    }

    // Getter和Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static void main(String[] args) {
        Person alice = new Person("Alice", 25);
        alice.greet();
    }
}

// 继承
public class Student extends Person {
    private String major;

    public Student(String name, int age, String major) {
        super(name, age);
        this.major = major;
    }

    @Override
    public void greet() {
        super.greet();
        System.out.println("我的专业是 " + major);
    }
}
"""

    python_code = """
# Python类
class Person:
    def __init__(self, name, age):
        self.name = name  # 公有属性 (Python没有真正的私有)
        self._age = age    # 约定俗成的"保护"属性

    def greet(self):
        print(f"你好, 我是 {self.name}")

    # 使用@property装饰器实现getter
    @property
    def age(self):
        return self._age

    # setter
    @age.setter
    def age(self, value):
        if value < 0:
            raise ValueError("年龄不能为负")
        self._age = value

# 创建实例
alice = Person("Alice", 25)
alice.greet()

# 继承
class Student(Person):
    def __init__(self, name, age, major):
        super().__init__(name, age)
        self.major = major

    def greet(self):
        super().greet()
        print(f"我的专业是 {self.major}")

# 多重继承
class TeachingAssistant(Student, Professor):
    pass

# 特殊方法 (类似Java的toString)
class Person:
    def __str__(self):
        return f"Person(name={self.name}, age={self.age})"
"""

    print_comparison(java_code, python_code)

    # 类演示
    print("\n🔍 类演示:")
    class DemoPerson:
        def __init__(self, name):
            self.name = name
        def greet(self):
            print(f"你好，我是{self.name}")

    p = DemoPerson("测试")
    p.greet()

def lesson_exceptions():
    """异常处理"""
    print("\n📚 异常处理")

    java_code = """
// Java异常处理
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("除数不能为零: " + e.getMessage());
} finally {
    System.out.println("这段代码总是执行");
}

// 抛出异常
public static void checkAge(int age) throws Exception {
    if (age < 0) {
        throw new Exception("年龄无效");
    }
}
"""

    python_code = """
# Python异常处理
try:
    result = 10 / 0
except ZeroDivisionError as e:
    print(f"除数不能为零: {e}")
else:
    print("没有发生异常时执行")
finally:
    print("这段代码总是执行")

# 抛出异常
def check_age(age):
    if age < 0:
        raise ValueError("年龄无效")

# 自定义异常
class MyCustomError(Exception):
    pass

# 上下文管理器 (类似Java的try-with-resources)
with open("file.txt", "r") as file:
    content = file.read()
    # 文件会自动关闭
"""

    print_comparison(java_code, python_code)

    # 异常演示
    print("\n🔍 异常演示:")
    try:
        age = int(input("请输入年龄: "))
        if age < 0:
            raise ValueError("年龄不能为负")
    except ValueError as e:
        print(f"输入错误: {e}")
    else:
        print(f"输入的年龄是: {age}")

def lesson_collections():
    """集合类型"""
    print("\n📚 集合类型")

    java_code = """
// Java集合
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// 列表
ArrayList<String> list = new ArrayList<>();
list.add("Apple");
list.add("Banana");

// 集合
HashSet<String> set = new HashSet<>();
set.add("Apple");
set.add("Apple");  // 重复，不会添加

// 映射
HashMap<String, Integer> map = new HashMap<>();
map.put("Apple", 1);
map.put("Banana", 2);
"""

    python_code = """
# Python集合类型

# 列表 (可变有序)
fruits = ["Apple", "Banana"]
fruits.append("Orange")
fruits.insert(1, "Mango")

# 元组 (不可变有序)
dimensions = (1920, 1080)

# 集合 (无序不重复)
unique_fruits = {"Apple", "Banana", "Apple"}  # 结果为 {"Apple", "Banana"}

# 字典 (键值对)
inventory = {
    "Apple": 10,
    "Banana": 5,
    "Orange": 8
}

# 字典操作
inventory["Mango"] = 3  # 添加
count = inventory.get("Apple", 0)  # 获取，不存在返回0
del inventory["Banana"]  # 删除

# 遍历字典
for fruit, quantity in inventory.items():
    print(f"{fruit}: {quantity}")
"""

    print_comparison(java_code, python_code)

    # 集合演示
    print("\n🔍 集合演示:")
    numbers = [1, 2, 2, 3, 4, 4, 4]
    unique_numbers = set(numbers)
    print(f"原始列表: {numbers}")
    print(f"去重后: {unique_numbers}")

def lesson_comprehensions():
    """推导式"""
    print("\n📚 推导式 (Python特有特性)")

    java_code = """
// Java中没有直接对应的语法，需要手动实现
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> squares = new ArrayList<>();
for (Integer num : numbers) {
    squares.add(num * num);
}

Map<String, Integer> wordLengths = new HashMap<>();
for (String word : Arrays.asList("apple", "banana", "cherry")) {
    wordLengths.put(word, word.length());
}
"""

    python_code = """
# 列表推导式
numbers = [1, 2, 3, 4, 5]
squares = [num ** 2 for num in numbers]  # [1, 4, 9, 16, 25]

# 带条件的列表推导式
even_squares = [num ** 2 for num in numbers if num % 2 == 0]  # [4, 16]

# 字典推导式
words = ["apple", "banana", "cherry"]
word_lengths = {word: len(word) for word in words}  # {'apple': 5, 'banana': 6, 'cherry': 6}

# 集合推导式
first_letters = {word[0] for word in words}  # {'a', 'b', 'c'}
"""

    print_comparison(java_code, python_code)

    # 推导式演示
    print("\n🔍 推导式演示:")
    numbers = [1, 2, 3, 4, 5]
    print(f"数字列表: {numbers}")
    print(f"平方数: {[x**2 for x in numbers]}")
    print(f"偶数平方: {[x**2 for x in numbers if x % 2 == 0]}")

def lesson_lambda():
    """Lambda函数"""
    print("\n📚 Lambda函数")

    java_code = """
// Java Lambda (Java 8+)
import java.util.Arrays;
import java.util.List;

List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 排序
names.sort((a, b) -> a.length() - b.length());

// 过滤
List<String> longNames = names.stream()
    .filter(name -> name.length() > 4)
    .collect(Collectors.toList());
"""

    python_code = """
# Python Lambda函数
names = ["Alice", "Bob", "Charlie"]

# 排序
names.sort(key=lambda x: len(x))  # 按长度排序

# 过滤
long_names = list(filter(lambda x: len(x) > 4, names))

# map函数
name_lengths = list(map(lambda x: len(x), names))

# 更简单的写法 (推荐使用列表推导式)
long_names = [name for name in names if len(name) > 4]
name_lengths = [len(name) for name in names]
"""

    print_comparison(java_code, python_code)

    # Lambda演示
    print("\n🔍 Lambda演示:")
    numbers = [1, 2, 3, 4, 5]
    print(f"原始数字: {numbers}")
    print(f"加倍后的数字: {list(map(lambda x: x*2, numbers))}")

def lesson_files():
    """文件操作"""
    print("\n📚 文件操作")

    java_code = """
// Java文件操作
import java.io.*;

// 读取文件
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// 写入文件
try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
    bw.write("Hello, World!");
} catch (IOException e) {
    e.printStackTrace();
}
"""

    python_code = """
# Python文件操作

# 读取文件 (推荐方式)
with open("file.txt", "r", encoding="utf-8") as file:
    for line in file:
        print(line.strip())

# 写入文件
with open("output.txt", "w") as file:
    file.write("Hello, World!\\n")
    file.write("第二行\\n")

# 读取所有内容
with open("file.txt", "r") as file:
    content = file.read()  # 整个文件内容作为字符串

# 读取所有行
with open("file.txt", "r") as file:
    lines = file.readlines()  # 返回行列表
"""

    print_comparison(java_code, python_code)

    # 文件操作演示
    print("\n🔍 文件操作演示:")
    try:
        with open("test.txt", "w") as f:
            f.write("这是一个测试文件\nPython文件操作很简单")

        with open("test.txt", "r") as f:
            print("\n文件内容:")
            print(f.read())
    except IOError as e:
        print(f"文件操作错误: {e}")

def lesson_modules():
    """模块和包"""
    print("\n📚 模块和包")

    java_code = """
// Java包和导入
package com.example.myapp;

import java.util.ArrayList;
import static java.lang.Math.PI;

// 使用导入的类
ArrayList<String> list = new ArrayList<>();
double radius = PI * 2;
"""

    python_code = """
# Python模块和导入

# 导入整个模块
import math
print(math.pi)

# 导入特定内容
from math import pi, sqrt
print(pi)
print(sqrt(16))

# 导入并重命名
import numpy as np
import pandas as pd

# 从包中导入
from mypackage.mymodule import myfunction

# 创建自己的模块
# 1. 创建一个.py文件 (如 mymodule.py)
# 2. 定义函数和变量
# 3. 在其他文件中使用: import mymodule

# 创建包
# 1. 创建一个目录
# 2. 添加__init__.py文件 (可以是空的)
# 3. 添加模块文件
# 4. 使用: from mypackage import mymodule
"""

    print_comparison(java_code, python_code)

    # 模块演示
    print("\n🔍 模块演示:")
    import math
    print(f"math模块的pi值: {math.pi}")
    print(f"4的平方根: {math.sqrt(4)}")

def show_menu():
    """显示学习菜单"""
    print("\n📖 Python学习菜单 (面向Java开发者)")
    print("=" * 50)
    for i, lesson in enumerate(Lesson, 1):
        print(f"{i}. {lesson.name.replace('_', ' ').title()}")
    print("=" * 50)

def main():
    """主程序"""
    print("欢迎来到Python学习程序 (面向Java开发者)!")

    while True:
        show_menu()
        try:
            choice = int(input("请选择要学习的内容 (输入数字): "))
            lesson = list(Lesson)[choice - 1]

            if lesson == Lesson.VARIABLES:
                lesson_variables()
            elif lesson == Lesson.DATA_TYPES:
                lesson_data_types()
            elif lesson == Lesson.OPERATORS:
                lesson_operators()
            elif lesson == Lesson.CONTROL_FLOW:
                lesson_control_flow()
            elif lesson == Lesson.FUNCTIONS:
                lesson_functions()
            elif lesson == Lesson.CLASSES:
                lesson_classes()
            elif lesson == Lesson.EXCEPTIONS:
                lesson_exceptions()
            elif lesson == Lesson.COLLECTIONS:
                lesson_collections()
            elif lesson == Lesson.COMPREHENSIONS:
                lesson_comprehensions()
            elif lesson == Lesson.LAMBDA:
                lesson_lambda()
            elif lesson == Lesson.FILES:
                lesson_files()
            elif lesson == Lesson.MODULES:
                lesson_modules()
            elif lesson == Lesson.EXIT:
                print("感谢使用Python学习程序，再见！")
                sys.exit()
            else:
                print("无效选择，请重新输入。")
        except (ValueError, IndexError):
            print("无效输入，请输入菜单中的数字。")
        except KeyboardInterrupt:
            print("\n检测到中断，退出程序。")
            sys.exit()

if __name__ == "__main__":
    main()