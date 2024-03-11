package fun.fengwk.convention4j.tracer.util;

import lombok.Builder;
import lombok.Data;

/**
 * @author fengwk
 */
@Builder
@Data
public class SpanAspectInfo {

    /**
     * span操作名
     */
    private final String value;

    /**
     * span别名
     */
    private final String alias;

    /**
     * span分类
     * @see io.opentracing.tag.Tags#SPAN_KIND_SERVER
     * @see io.opentracing.tag.Tags#SPAN_KIND_CLIENT
     * @see io.opentracing.tag.Tags#SPAN_KIND_PRODUCER
     * @see io.opentracing.tag.Tags#SPAN_KIND_CONSUMER
     */
    private final String kind;

    /**
     * 传播行为
     */
    private final SpanPropagation propagation;

}
