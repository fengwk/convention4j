package fun.fengwk.convention4j.common;

import javax.annotation.Nullable;

/**
 * @author fengwk
 */
public class ConvertUtils {

    public static final int INT_TRUE = 1;
    public static final int INT_FALSE = 0;

    private ConvertUtils() {}

    /**
     * 使用int表示输入的boolean值。
     *
     * @param b
     * @return
     */
    public static int bool2int(boolean b) {
        return b ? INT_TRUE : INT_FALSE;
    }

    /**
     * 使用Integer表示输入的Boolean值。
     *
     * @param b
     * @return
     */
    @Nullable
    public static Integer bool2int(Boolean b) {
        return b == null ? null : bool2int((boolean) b);
    }

    /**
     * 使用boolean表示输入的int值。
     *
     * @param i
     * @return
     */
    public static boolean int2bool(int i) {
        return i == INT_TRUE;
    }

    /**
     * 使用Boolean表示输入的Integer值。
     *
     * @param i
     * @return
     */
    @Nullable
    public static Boolean int2bool(Integer i) {
        return i == null ? null : int2bool((int) i);
    }

}
