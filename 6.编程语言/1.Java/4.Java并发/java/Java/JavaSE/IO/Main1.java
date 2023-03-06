package Java.JavaSE.IO;

import org.junit.Test;

import java.util.Arrays;
import java.util.Scanner;

public class Main1 {
        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            String str = sc.nextLine();
            int length = str.length();
            int count = 0;
            for (int i = length - 1; i >= 0; i--) {
                if (str.charAt(i)==' ') { // 或者 if (str.substring(i, i + 1).equals(" ")) {
                    break;
                }
                count++;
            }
            System.out.println(count);
        }


        //String.split() 一个或多个空格分割字符串
        @Test
        public void test(){
            String s = "1 12    4";
            String[] s1 = s.split("\\s+");//匹配任意一个或多个空格

            Arrays.sort(s1,(o1, o2)->Integer.parseInt(o1)>Integer.parseInt(o2)?1:-1);
            System.out.println(Arrays.toString(s1));

            /**
             * 注：Arrays.sort方法与Collections.sort方法类似,，都可以通过比较器Comparator<?>自定义排序规则
             *
             * 比较器Comparator<?>中public int compare(Integer o1, Integer o2) 函数：
             * 1.默认升序排序
             * 2.重写时的比较规则
             *      1）如果要按照升序排序，则01大于02返回（正数），相等返回0，o1 小于o2，返回（负数）
             *      2）如果要按照降序排序，则01大于02返回（负数），相等返回0，o1 小于o2，返回（正数）
             *
             *      另：升序相当于o1 - o2，降序相当于o2 - o1
             */
        }
}
