package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;
import lombok.AllArgsConstructor;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class FeignInternalInvokerInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ResultInternalInvokerUtils.setIgnoreErrorHttpStatus(template::header);
    }

}
