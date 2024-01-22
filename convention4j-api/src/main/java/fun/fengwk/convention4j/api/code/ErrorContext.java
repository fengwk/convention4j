package fun.fengwk.convention4j.api.code;

import java.util.Map;

/**
 * 错误信息上下文。
 *
 * @author fengwk
 */
public interface ErrorContext {

    /**
     * 获取错误信息。
     *
     * @return 错误编码。
     */
    Map<String, Object> getErrors();

}
