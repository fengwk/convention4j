package fun.fengwk.convention4j.common;

/**
 * 使用int类型表示bool。
 *
 * @author fengwk
 */
public class IntBool {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    private IntBool() {}

    /**
     * 使用int表示输入的boolean值。
     *
     * @param b
     * @return
     */
    public static int bool2int(boolean b) {
        return b ? TRUE : FALSE;
    }

    /**
     * 使用Integer表示输入的Boolean值。
     *
     * @param b
     * @return
     */
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
        return i == TRUE;
    }

    /**
     * 使用Boolean表示输入的Integer值。
     *
     * @param i
     * @return
     */
    public static Boolean int2bool(Integer i) {
        return i == null ? null : int2bool((int) i);
    }

}
