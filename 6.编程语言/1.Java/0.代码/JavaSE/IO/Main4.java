package JavaSE.IO;

import java.util.Scanner;
/*
一定要注意nextLine()和其他next()的！连用！
 */
public class Main4 {
    //next()一定要读取到有效字符后才可以结束输入，对输入有效字符之前遇到的空格键、Tab键或Enter键等结束符，next()方法会自动将其去掉，
    //nextLine()会自动读取被next()去掉的Enter作为他的结束符，所以没办法给s2从键盘输入值。
    public static void main(String[] args) throws Exception {
        String s1,s2;
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入第一个字符串：");
        s1=sc.next();
        //sc.nextLine();//解决的办法是：在每一个 next()、nextDouble() 、 nextFloat()、nextInt() 等语句之后加一个nextLine()语句，将被next()去掉的Enter结束符过滤掉。
        System.out.print("请输入第二个字符串：");
        s2=sc.nextLine();
        System.out.println("输入的字符串是："+s1+" "+s2);

    }
}
