package fun.fengwk.convention4j.example.i18n;

import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManager;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author fengwk
 */
public class StringManagerExample {

    public static void main(String[] args) {
        ClassLoader cl = StringManagerExample.class.getClassLoader();

        // 英语
        StringManagerFactory enStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.ENGLISH,
                        cl, AggregateResourceBundle.CONTROL));
        StringManager enStringManager = enStringManagerFactory.getStringManager("fun.fengwk.convention4j.example.i18n.Strings.");
        System.out.println(enStringManager.getString("greeting", MapUtils.newMap("name", "fengwk")));
        // 输出：hi, fengwk.

        // 中文
        StringManagerFactory cnStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.CHINA,
                        cl, AggregateResourceBundle.CONTROL));
        StringManager cnStringManager = cnStringManagerFactory.getStringManager("fun.fengwk.convention4j.example.i18n.Strings.");
        System.out.println(cnStringManager.getString("greeting", MapUtils.newMap("name", "fengwk")));
        // 输出：你好，fengwk。
    }

}
