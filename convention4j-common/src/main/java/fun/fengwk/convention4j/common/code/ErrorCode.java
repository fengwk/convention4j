package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.common.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 错误码。
 * 
 * <p>
 * 描述：
 * 在业务系统中使用错误码来替代复杂的异常类继承结构可以保持业务代码的简洁以及错误信息的高内聚，
 * 同时错误码相比于异常进行进程间传递时更加轻量。
 * </p>
 * 
 * <p>
 * code编码规约：域_四位数字编号<br/>
 * 域：由简短的描述组成，必须符合格式^[a-zA-Z]+$
 * </p>
 *
 * <p>
 * 示例：
 * <ul>
 * <li>C_0001：表示调用参数异常</li>
 * <li>C_0002：表示系统状态异常</li>
 * </ul>
 * </p>
 * 
 * @author fengwk
 */
public interface ErrorCode extends Code {

    /**
     * 错误码表示分隔符。
     */
    String SEPARATOR = "_";

    /**
     * 域的模式。
     */
    Pattern REGEX_DOMAIN = Pattern.compile("^[a-zA-Z]+$");

    /**
     * 四位数字编号的模式。
     */
    Pattern REGEX_NUM = Pattern.compile("^[0-9]{4}$");

    /**
     * 获取当前异常码码值，在使用异常码模式开发的过程中应该确保不同类型的错误具有不同的码值。
     * 
     * @return
     */
    @Override
    String getCode();
    
    /**
     * 获取错误码信息，该信息应该简洁明了地阐述当前错误原因。
     * 
     * @return nullable
     */
    String getMessage();

    /**
     * 获取当前错误码包含的错误信息。
     *
     * @return
     */
    Map<String, ?> getErrors();

    /**
     * 获取当前错误码的{@link ThrowableErrorCode}视图。
     *
     * @return
     */
    default ThrowableErrorCode asThrowable() {
        return new ThrowableErrorCode(this);
    }

    /**
     * 获取当前错误码的{@link ThrowableErrorCode}视图。
     *
     * @param cause 造成原因，允许用null来表示不存在或未知。
     * @return
     */
    default ThrowableErrorCode asThrowable(Throwable cause) {
        return cause == null ? asThrowable() : new ThrowableErrorCode(this, cause);
    }

    /**
     * 将指定异常转为{@link ThrowableErrorCode}视图。
     *
     * @param err not null，异常。
     * @param asThrowableFunc not null，如果异常无法自动转为ThrowableErrorCode，则需要调用asThrowableFunc函数进行转换。
     * @param <T>
     * @return
     */
    static <T extends Throwable> ThrowableErrorCode asThrowable(
            T err, Function<T, ThrowableErrorCode> asThrowableFunc) {
        if (err == null) {
            throw new NullPointerException("err cannot be null");
        }
        if (asThrowableFunc == null) {
            throw new NullPointerException("asThrowableFunc cannot be null");
        }

        if (err instanceof ThrowableErrorCode) {
            return (ThrowableErrorCode) err;
        } else if (err instanceof ErrorCode) {
            return ((ErrorCode) err).asThrowable(err);
        } else {
            return asThrowableFunc.apply(err);
        }
    }

    /**
     * 业务错误码：使用“域_四位数字编号”方式编码code。
     *
     * @param domain not empty
     * @param num not empty
     * @return
     */
    static String encodeCode(String domain, String num) {
        if (StringUtils.isEmpty(domain)) {
            throw new IllegalArgumentException("domain cannot be empty");
        }
        if (StringUtils.isEmpty(num)) {
            throw new IllegalArgumentException("num cannot be empty");
        }

        if (!REGEX_DOMAIN.matcher(domain).matches()) {
            throw new IllegalArgumentException("domain '" + domain + "' format error");
        }
        if (!REGEX_NUM.matcher(num).matches()) {
            throw new IllegalArgumentException("num '" + num + "' format error");
        }

        return domain + SEPARATOR + num;
    }

    /**
     * 返回长度为2的String数组，索引0为domain，索引1为num。
     *
     * @param errorCode not empty
     * @return
     * @throws IllegalArgumentException 如果errorCode格式错误将抛出该异常。
     */
    static String[] decodeCode(String errorCode) {
        int idx;
        String domain, num;
        if (errorCode != null && errorCode.length() >= 6
                && (idx = errorCode.indexOf(SEPARATOR)) != -1
                && REGEX_DOMAIN.matcher(domain = errorCode.substring(0, idx)).matches()
                && REGEX_NUM.matcher(num = errorCode.substring(idx + 1)).matches()) {
            return new String[] { domain, num };
        } else {
            throw new IllegalArgumentException("errorCode '" + errorCode + "' format error");
        }
    }

    /**
     * 校验错误码格式。
     *
     * @param errorCode
     * @return
     */
    static boolean validateErrorCodeFormat(String errorCode) {
        if (errorCode == null || errorCode.length() < 6) {
            return false;
        }

        int idx = errorCode.indexOf(SEPARATOR);
        if (idx == -1) {
            return false;
        }

        if (!REGEX_DOMAIN.matcher(errorCode.substring(0, idx)).matches()) {
            return false;
        }
        if (!REGEX_NUM.matcher(errorCode.substring(idx + 1)).matches()) {
            return false;
        }

        return true;
    }

}
