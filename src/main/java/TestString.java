import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestString {
    public static void main(String[] args) {
        String str = "https://allure.autotests.cloud/project/2230/test-cases/18007?treeId=0";
        Matcher matcher = Pattern.compile("\\d{5}+").matcher(str);
        matcher.find();
        String s = matcher.group();
        System.out.println(s);
    }
}
