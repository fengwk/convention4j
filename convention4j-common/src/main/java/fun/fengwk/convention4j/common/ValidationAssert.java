//package fun.fengwk.convention4j.common;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Collection;
//
///**
// * 断言器，配合validation-api使用。
// *
// * @author fengwk
// */
//public class ValidationAssert {
//
//    private ValidationAssert() {}
//
//    /**
//     * @see javax.validation.constraints.AssertFalse
//     * @param bool
//     * @param message
//     */
//    public static void isFalse(Boolean bool, String message) {
//        if (bool != null && bool) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.AssertTrue
//     * @param bool
//     * @param message
//     */
//    public static void isTrue(Boolean bool, String message) {
//        if (bool != null && !bool) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param bigDecimal
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(BigDecimal bigDecimal, String value, boolean inclusive, String message) {
//        if (bigDecimal != null
//                && (inclusive ? bigDecimal.compareTo(new BigDecimal(value)) > 0
//                : bigDecimal.compareTo(new BigDecimal(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param bigDecimal
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(BigDecimal bigDecimal, String value, String message) {
//        isDecimalMax(bigDecimal, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param bigInteger
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(BigInteger bigInteger, String value, boolean inclusive, String message) {
//        if (bigInteger != null
//                && (inclusive ? bigInteger.compareTo(new BigInteger(value)) > 0
//                : bigInteger.compareTo(new BigInteger(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param bigInteger
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(BigInteger bigInteger, String value, String message) {
//        isDecimalMax(bigInteger, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param cs
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(CharSequence cs, String value, boolean inclusive, String message) {
//        isDecimalMax(tryToBigDecimal(cs), value, inclusive, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param cs
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(CharSequence cs, String value, String message) {
//        isDecimalMax(cs, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param b
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(Byte b, String value, boolean inclusive, String message) {
//        if (b != null
//                && (inclusive ? b.compareTo(Byte.valueOf(value)) > 0
//                : b.compareTo(Byte.valueOf(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param b
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(Byte b, String value, String message) {
//        isDecimalMax(b, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param s
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(Short s, String value, boolean inclusive, String message) {
//        if (s != null
//                && (inclusive ? s.compareTo(Short.valueOf(value)) > 0
//                : s.compareTo(Short.valueOf(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param s
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(Short s, String value, String message) {
//        isDecimalMax(s, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param i
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(Integer i, String value, boolean inclusive, String message) {
//        if (i != null
//                && (inclusive ? i.compareTo(Integer.valueOf(value)) > 0
//                : i.compareTo(Integer.valueOf(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param i
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(Integer i, String value, String message) {
//        isDecimalMax(i, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param l
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMax(Long l, String value, boolean inclusive, String message) {
//        if (l != null
//                && (inclusive ? l.compareTo(Long.valueOf(value)) > 0
//                : l.compareTo(Long.valueOf(value)) >= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMax
//     * @param l
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMax(Long l, String value, String message) {
//        isDecimalMax(l, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param bigDecimal
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(BigDecimal bigDecimal, String value, boolean inclusive, String message) {
//        if (bigDecimal != null
//                && (inclusive ? bigDecimal.compareTo(new BigDecimal(value)) < 0
//                : bigDecimal.compareTo(new BigDecimal(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param bigDecimal
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(BigDecimal bigDecimal, String value, String message) {
//        isDecimalMin(bigDecimal, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param bigInteger
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(BigInteger bigInteger, String value, boolean inclusive, String message) {
//        if (bigInteger != null
//                && (inclusive ? bigInteger.compareTo(new BigInteger(value)) < 0
//                : bigInteger.compareTo(new BigInteger(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param bigInteger
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(BigInteger bigInteger, String value, String message) {
//        isDecimalMin(bigInteger, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param cs
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(CharSequence cs, String value, boolean inclusive, String message) {
//        isDecimalMin(tryToBigDecimal(cs), value, inclusive, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param cs
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(CharSequence cs, String value, String message) {
//        isDecimalMin(cs, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param b
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(Byte b, String value, boolean inclusive, String message) {
//        if (b != null
//                && (inclusive ? b.compareTo(Byte.valueOf(value)) < 0
//                : b.compareTo(Byte.valueOf(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param b
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(Byte b, String value, String message) {
//        isDecimalMin(b, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param s
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(Short s, String value, boolean inclusive, String message) {
//        if (s != null
//                && (inclusive ? s.compareTo(Short.valueOf(value)) < 0
//                : s.compareTo(Short.valueOf(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param s
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(Short s, String value, String message) {
//        isDecimalMin(s, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param i
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(Integer i, String value, boolean inclusive, String message) {
//        if (i != null
//                && (inclusive ? i.compareTo(Integer.valueOf(value)) < 0
//                : i.compareTo(Integer.valueOf(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param i
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(Integer i, String value, String message) {
//        isDecimalMin(i, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param l
//     * @param value
//     * @param inclusive
//     * @param message
//     */
//    public static void isDecimalMin(Long l, String value, boolean inclusive, String message) {
//        if (l != null
//                && (inclusive ? l.compareTo(Long.valueOf(value)) < 0
//                : l.compareTo(Long.valueOf(value)) <= 0)) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.DecimalMin
//     * @param l
//     * @param value
//     * @param message
//     */
//    public static void isDecimalMin(Long l, String value, String message) {
//        isDecimalMin(l, value, true, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.Digits
//     * @param num
//     * @param integer
//     * @param fraction
//     * @param message
//     */
//    public static void isDigits(Number num, int integer, int fraction, String message) {
//        if (num == null) {
//            return;
//        }
//
//        BigDecimal bigNum;
//        if (num instanceof BigDecimal) {
//            bigNum = (BigDecimal) num;
//        }
//        else {
//            bigNum = new BigDecimal(num.toString()).stripTrailingZeros();
//        }
//
//        int integerPartLength = bigNum.precision() - bigNum.scale();
//        int fractionPartLength = Math.max(bigNum.scale(), 0);
//
//        if (integerPartLength < integer || fractionPartLength < fraction) {
//            throw new IllegalArgumentException(message);
//        }
//    }
//
//    /**
//     * @see javax.validation.constraints.Digits
//     * @param cs
//     * @param integer
//     * @param fraction
//     * @param message
//     */
//    public static void isDigits(CharSequence cs, int integer, int fraction, String message) {
//        isDigits(tryToBigDecimal(cs), integer, fraction, message);
//    }
//
//    /**
//     * @see javax.validation.constraints.NotBlank
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotEmpty
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
//     * @see javax.validation.constraints.NotNull
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
//     * @see javax.validation.constraints.Null
//     * @param obj
//     * @param message
//     */
//    public static void isNull(Object obj, String message) {
//        if (obj == null) {
//            throw new NullPointerException(message);
//        }
//    }
//
//    private static BigDecimal tryToBigDecimal(CharSequence cs) {
//        try {
//            return new BigDecimal(cs.toString());
//        } catch (NumberFormatException nfe) {
//            return null;
//        }
//    }
//
//}
