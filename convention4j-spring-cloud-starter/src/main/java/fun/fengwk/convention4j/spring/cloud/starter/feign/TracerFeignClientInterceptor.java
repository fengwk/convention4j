package fun.fengwk.convention4j.spring.cloud.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

/**
 * @author fengwk
 */
public class TracerFeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Tracer tracer = GlobalTracer.get();
        Span activeSpan = tracer.activeSpan();
        if (activeSpan != null) {
            tracer.inject(activeSpan.context(), FeignClientInject.FORMAT, template);
        }
    }

}
