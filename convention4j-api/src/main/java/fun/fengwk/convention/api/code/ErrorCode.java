package fun.fengwk.convention.api.code;

import java.util.Objects;

/**
 * 错误码。
 * 
 * <p>
 * 描述：
 * 在业务系统中使用错误码来替代复杂的异常类继承结构可以保持业务代码的简洁以及错误信息的高内聚，
 * 同时错误码相比于异常能进行更轻量的进程间传递。
 * </p>
 * 
 * <p>
 * code编码规约，参考<a href="https://blog.csdn.net/alitech2017/article/details/107042035/">错误码如何设计才合理？</a>：
 * 错误产生来源_[业务域_]四位数字编号
 * 
 * <p>
 * 错误产生来源：
 * <ul>
 * <li>A：表示当前错误来自于调用者，例如调用者传递了错误的入参</li>
 * <li>B：表示当前错误来自于当前系统，例如当前系统发生OOM</li>
 * <li>C：表示当前错误来自于依赖系统，例如发生RPC调用错误</li>
 * </ul>
 * 例如使用A_0001表示调用参数异常，使用B_0001表示系统状态异常。
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
     * 表示当前错误来自于调用者。
     */
    String SOURCE_A = "A";
    
    /**
     * 表示当前错误来自于当前系统。
     */
    String SOURCE_B = "B";
    
    /**
     * 表示当前错误来自于依赖系统。
     */
    String SOURCE_C = "C";
    
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
     * @return
     */
    String getMessage();
    
    /**
     * 检查当前错错误产生来源，详见{@link ErrorCode}文档。
     * 
     * @return
     */
    default boolean sourceOf(String source) {
        return getCode().startsWith(source);
    }

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
        return new ThrowableErrorCode(this, cause);
    }
    
    /**
     * 使用“错误产生来源_四位数字编号”方式编码code。
     * 
     * @param source not null
     * @param num not null
     * @return
     */
    static String encodeCode(String source, String num) {
        Objects.requireNonNull(source, "Source cannot be null");
        Objects.requireNonNull(num, "Num cannot be null");
        if (source.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Source format error");
        }
        if (num.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Num format error");
        }

        return source + SEPARATOR + num;
    }

    /**
     * 使用“错误产生来源_业务域_四位数字编号”方式编码code。
     *
     * @param source not null
     * @param domain not null
     * @param num not null
     * @return
     */
    static String encodeCode(String source, String domain, String num) {
        Objects.requireNonNull(source, "Source cannot be null");
        Objects.requireNonNull(domain, "Domain cannot be null");
        Objects.requireNonNull(num, "Num cannot be null");
        if (source.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Source format error");
        }
        if (domain.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Domain format error");
        }
        if (num.contains(SEPARATOR)) {
            throw new IllegalArgumentException("Num format error");
        }

        return source + SEPARATOR + domain + SEPARATOR + num;
    }
    
}
