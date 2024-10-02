package fun.fengwk.convention4j.springboot.starter.web.result;

import fun.fengwk.convention4j.api.result.Result;

/**
 * @author fengwk
 */
public interface WebExceptionResultHandler {

    boolean support(Throwable ex);

    <T> Result<T> handle(WebExceptionResultHandlerContext context);

}
