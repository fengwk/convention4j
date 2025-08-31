package fun.fengwk.convention4j.springboot.starter.web.result;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author fengwk
 */
@Slf4j
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Result<?>> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType,
                                     Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                     ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null) {
            HttpHeaders headers = request.getHeaders();
            if (ResultInternalInvokerUtils.isIgnoreErrorHttpStatus(headers::getFirst)) {
                response.setStatusCode(HttpStatus.OK);
            } else {
                HttpStatusCode httpStatusCode;
                try {
                    httpStatusCode = HttpStatusCode.valueOf(body.getStatus());
                } catch (IllegalArgumentException ex) {
                    log.error("Invalid http status code: {}", body.getStatus());
                    httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                response.setStatusCode(httpStatusCode);
            }
        }
        return body;
    }

}
