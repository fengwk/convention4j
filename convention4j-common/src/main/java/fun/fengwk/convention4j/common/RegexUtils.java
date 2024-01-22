package fun.fengwk.convention4j.common;

import java.util.regex.Pattern;

/**
 * 常用正则表达式工具。
 * @author fengwk
 */
public class RegexUtils {

    private final static Pattern REGEX_EMAIL = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private final static Pattern REGEX_CHINESE_MOBILE = Pattern.compile(
        "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");

    private RegexUtils() {}

    /**
     * 检查指定邮箱是否合法。
     * @param email 待测试的邮箱。
     * @return 如果合法返回true，否则返回false。
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return REGEX_EMAIL.matcher(email).matches();
    }

    /**
     * 检查指定中国手机号是否合法。
     * @param mobile 待测试的手机号。
     * @return 如果合法返回true，否则返回false。
     */
    public static boolean isChineseMobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return false;
        }
        return REGEX_CHINESE_MOBILE.matcher(mobile).matches();
    }

}
