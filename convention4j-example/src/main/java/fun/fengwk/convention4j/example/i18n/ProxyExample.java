package fun.fengwk.convention4j.example.i18n;

import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author fengwk
 */
public class ProxyExample {

    public static void main(String[] args) {
        ClassLoader cl = StringManagerExample.class.getClassLoader();

        // 英语
        StringManagerFactory enStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.ENGLISH,
                        cl, AggregateResourceBundle.CONTROL));
        Strings enStrings = enStringManagerFactory.getStringManagerProxy(Strings.class, cl);
        System.out.println(enStrings.greeting("fengwk"));
        // 输出：hi, fengwk.

        // 中文
        StringManagerFactory cnStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.CHINA,
                        cl, AggregateResourceBundle.CONTROL));
        Strings cnStrings = cnStringManagerFactory.getStringManagerProxy(Strings.class, cl);
        System.out.println(cnStrings.greeting("fengwk"));
        // 输出：你好，fengwk。
    }

}
