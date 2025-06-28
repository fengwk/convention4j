package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import fun.fengwk.convention4j.springboot.starter.result.ResultInternalInvokerUtils;

/**
 * @author fengwk
 */
public class InternalInvokerFeignClinetInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ResultInternalInvokerUtils.setIgnoreErrorHttpStatus(template::header);
    }

}
