package Java.JavaSE.IO;

import java.io.IOException;
import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        char[] chars1  = scanner.nextLine().toLowerCase().toCharArray();
        char[] chars2  = scanner.nextLine().toLowerCase().toCharArray();
        int count = 0;
        for (int i = 0; i < chars1.length; i++) {
            if  (chars1[i] == chars2[0]) {
                count++;
            }
        }
        System.out.println(count);
    }
}
