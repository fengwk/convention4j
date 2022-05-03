package fun.fengwk.convention4j.common;

/**
 * @author fengwk
 */
public class StringUtils {

    private StringUtils() {}

    /**
     * 空白字符串。
     */
    public static final String EMPTY = "";

    /**
     * 检查str是否为null，或者长度为0。
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 检查str是否是一个长度大于0的字符串。
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * 检查str是否为null，或者只包含空白元素。
     *
     * @see Character#isWhitespace(char)
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查str是否非空，并且不全由空白符组成。
     *
     * @see Character#isWhitespace(char)
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较cs1[lo1..hi1)和cs2[lo2..hi2)是否相等。
     *
     * @param cs1 not null
     * @param lo1 cs1的低位，包含。
     * @param hi1 cs1的高位，不包含。
     * @param cs2 not null
     * @param lo2 cs2的低位，包含。
     * @param hi2 cs2的高位，不包含。
     * @return
     */
    public static boolean equals(CharSequence cs1, int lo1, int hi1, CharSequence cs2, int lo2, int hi2) {
        if (hi1 - lo1 != hi2 - lo2) {
            return false;
        }

        while (lo1 < hi1 && lo2 < hi2) {
            if (cs1.charAt(lo1) != cs2.charAt(lo2)) {
                return false;
            }

            lo1++;
            lo2++;
        }

        return lo1 == hi1 && lo2 == hi2;
    }

    /**
     * 比较cs1[lo1..lo1+length)和cs2[lo2..lo2+length)是否相等。
     *
     * @param cs1
     * @param lo1
     * @param cs2
     * @param lo2
     * @param length
     * @return
     */
    public static boolean equals(CharSequence cs1, int lo1, CharSequence cs2, int lo2, int length) {
        return equals(cs1, lo1, lo1 + length, cs2, lo2, lo2 + length);
    }

}
