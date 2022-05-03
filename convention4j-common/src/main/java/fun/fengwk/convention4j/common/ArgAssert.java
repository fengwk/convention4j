//package fun.fengwk.convention4j.common;
//
//import java.util.Collection;
//
///**
// * @author fengwk
// */
//public class ArgAssert {
//
//    private ArgAssert() {}
//
//    /**
//     * @param cs
//     * @param message
//     */
//    public static void isNotBlank(CharSequence cs, String message) {
//        if (cs == null || cs.length() == 0) {
//            throw new IllegalArgumentException(message);
//        }
//
//        for (int i = 0; i < cs.length(); i++) {
//            if (!Character.isWhitespace(cs.charAt(i))) {
//                return;
//            }
//        }
//        throw new IllegalArgumentException(message);
//    }
//
//    /**
//     * @param cs
//     * @param message
//     */
//    public static void isNotEmpty(CharSequence cs, String message) {
//        if (cs == null || cs.length() == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param collection
//     * @param message
//     */
//    public static void isNotEmpty(Collection<?> collection, String message) {
//        if (collection == null || collection.isEmpty()) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param objs
//     * @param message
//     */
//    public static <T> void isNotEmpty(T[] objs, String message) {
//        if (objs == null || objs.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param bytes
//     * @param message
//     */
//    public static void isNotEmpty(byte[] bytes, String message) {
//        if (bytes == null || bytes.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param shorts
//     * @param message
//     */
//    public static void isNotEmpty(short[] shorts, String message) {
//        if (shorts == null || shorts.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param chars
//     * @param message
//     */
//    public static void isNotEmpty(char[] chars, String message) {
//        if (chars == null || chars.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param ints
//     * @param message
//     */
//    public static void isNotEmpty(int[] ints, String message) {
//        if (ints == null || ints.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param longs
//     * @param message
//     */
//    public static void isNotEmpty(long[] longs, String message) {
//        if (longs == null || longs.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param floats
//     * @param message
//     */
//    public static void isNotEmpty(float[] floats, String message) {
//        if (floats == null || floats.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param doubles
//     * @param message
//     */
//    public static void isNotEmpty(double[] doubles, String message) {
//        if (doubles == null || doubles.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param booleans
//     * @param message
//     */
//    public static void isNotEmpty(boolean[] booleans, String message) {
//        if (booleans == null || booleans.length == 0) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param obj
//     * @param message
//     */
//    public static void isNotNull(Object obj, String message) {
//        if (obj == null) {
//            throw new NullPointerException(message);
//        }
//    }
//
//    /**
//     * @param expression
//     * @param message
//     */
//    public static void isTrue(boolean expression, String message) {
//        if (!expression) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @param expression
//     * @param message
//     */
//    public static void isFalse(boolean expression, String message) {
//        if (expression) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//}
