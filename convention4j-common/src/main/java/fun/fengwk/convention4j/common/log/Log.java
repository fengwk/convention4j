package fun.fengwk.convention4j.common.log;

import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.expression.ExpressionException;
import fun.fengwk.convention4j.common.expression.OgnlExpressionParser;
import fun.fengwk.convention4j.common.gson.GlobalGson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link Log}定义了一套规范的日志输出格式并且提供了简单的方法输出这种格式的日志。
 * 其中日志规范输出应该包含以下两部分：
 * <ul>
 *     <li>记录（必须）：日志信息的主体内容，主要供人来查阅。</li>
 *     <li>上下文（可选）：产生日志时的上下文参数，必须记录足以排查产生当前日志问题的上下文信息，以便人来查阅定位日志产生的原因，或者供脚本进行日志存档或数据修复等工作。</li>
 * </ul>
 *
 * @author fengwk
 */
public class Log {

    private static final OgnlExpressionParser<Map<String, ?>> EXPRESSION_PARSER = new OgnlExpressionParser<>();

    private final Logger delegate;

    /**
     *
     * @param name not null
     */
    public Log(String name) {
        this(LoggerFactory.getLogger(name));
    }

    /**
     *
     * @param clazz not null
     */
    public Log(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    /**
     *
     * @param delegate not null
     */
    public Log(Logger delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /**
     *
     * @param record 日志记录
     * @param kvs 上下文键值对，最后一个元素可以是一个Throwable，将打印其异常信息
     */
    public void error(String record, Object... kvs) {
        log(delegate::isErrorEnabled, delegate::error, delegate::error, delegate::error, delegate::error, record, kvs);
    }

    /**
     *
     * @param record 日志记录
     * @param kvs 上下文键值对，最后一个元素可以是一个Throwable，将打印其异常信息
     */
    public void warn(String record, Object... kvs) {
        log(delegate::isWarnEnabled, delegate::warn, delegate::warn, delegate::warn, delegate::warn, record, kvs);
    }

    /**
     *
     * @param record 日志记录
     * @param kvs 上下文键值对，最后一个元素可以是一个Throwable，将打印其异常信息
     */
    public void debug(String record, Object... kvs) {
        log(delegate::isDebugEnabled, delegate::debug, delegate::debug, delegate::debug, delegate::debug, record, kvs);
    }

    /**
     *
     * @param record 日志记录
     * @param kvs 上下文键值对，最后一个元素可以是一个Throwable，将打印其异常信息
     */
    public void info(String record, Object... kvs) {
        log(delegate::isInfoEnabled, delegate::info, delegate::info, delegate::info, delegate::info, record, kvs);
    }

    /**
     *
     * @param record 日志记录
     * @param kvs 上下文键值对，最后一个元素可以是一个Throwable，将打印其异常信息
     */
    public void trace(String record, Object... kvs) {
        log(delegate::isTraceEnabled, delegate::trace, delegate::trace, delegate::trace, delegate::trace, record, kvs);
    }

    private void log(EnabledFunc enabledFunc,
                     LogFunc1 logFunc1,
                     LogFunc2 logFunc2,
                     LogFunc3 logFunc3,
                     LogFunc4 logFunc4,
                     String record,
                     Object... kvs) {
        if (enabledFunc.isEnabled()) {
            Map<String, Object> ctx = null;
            Throwable t = null;

            if (kvs != null && kvs.length > 0) {
                int len = kvs.length;
                if (len % 2 != 0 && kvs[len - 1] instanceof Throwable) {
                    t = (Throwable) kvs[len - 1];
                    len--;
                }

                if (len % 2 != 0) {
                    delegate.warn("kvs error {}", MapUtils.newMap("kvs", kvs));
                }

                if (len > 0) {
                    ctx = new LinkedHashMap<>();
                    for (int i = 0; i < len; i += 2) {
                        ctx.put(String.valueOf(kvs[i]), kvs[i + 1]);
                    }
                }
            }

            Map<String, Object> logCtx = LogContext.asMapView();
            if (!logCtx.isEmpty()) {
                if (ctx == null) {
                    ctx = new LinkedHashMap<>();
                }
                ctx.putAll(logCtx);
            }

            String parsedRecord = record;
            if (ctx != null) {
                try {
                    parsedRecord = EXPRESSION_PARSER.parse(record, ctx);
                } catch (ExpressionException e) {
                    // 降级策略打印未解析过的日志
                    delegate.warn("parse record error {}", GlobalGson.getInstance().toJson(
                            MapUtils.newMap("record", record, "ctx", ctx, "t", t)));
                }
            }

            if (ctx != null) {
                if (t != null) {
                    logFunc4.log(parsedRecord + " {}", GlobalGson.getInstance().toJson(ctx), t);
                } else {
                    logFunc3.log(parsedRecord + " {}", GlobalGson.getInstance().toJson(ctx));
                }
            } else {
                if (t != null) {
                    logFunc2.log(parsedRecord, t);
                } else {
                    logFunc1.log(parsedRecord);
                }
            }
        }
    }

    @FunctionalInterface
    interface EnabledFunc {
        boolean isEnabled();
    }

    @FunctionalInterface
    interface LogFunc1 {
        void log(String msg);
    }

    @FunctionalInterface
    interface LogFunc2 {
        void log(String msg, Throwable t);
    }

    @FunctionalInterface
    interface LogFunc3 {
        void log(String format, Object arg);
    }

    @FunctionalInterface
    interface LogFunc4 {
        void log(String format, Object arg1, Object arg2);
    }

}
