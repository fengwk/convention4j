package fun.fengwk.convention4j.common.log;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 日志上下文。
 *
 * @author fengwk
 */
public class LogContext {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL_CONTEXT =
            ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * 向当前线程的日志上下文中添加键值对。
     *
     * @param key
     * @param val
     */
    public static void put(String key, Object val) {
        THREAD_LOCAL_CONTEXT.get().put(key, val);
    }

    /**
     * 清理当前线程的日志上下文。
     */
    public static void clear() {
        THREAD_LOCAL_CONTEXT.get().clear();
    }

    /**
     * 获取当前线程日志上下文的集合视图。
     *
     * @return
     */
    public static Map<String, Object> asMapView() {
        return Collections.unmodifiableMap(THREAD_LOCAL_CONTEXT.get());
    }

}
