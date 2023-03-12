import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) {
        /*Long a = 192168113115L & 2552552400L;
        Long b = 192168113200L & 2552552400L;;
        System.out.println(a);
        System.out.println(b);*/

        String s = "2023-01-31";
        String regex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$|无固定到期日";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            System.out.println("1");
        } else {
            System.out.println("0");
        }

    }
}
