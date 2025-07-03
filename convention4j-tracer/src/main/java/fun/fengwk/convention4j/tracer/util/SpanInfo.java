package fun.fengwk.convention4j.tracer.util;

import fun.fengwk.convention4j.common.util.NullSafe;
import io.opentracing.tag.Tags;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public final class SpanInfo {

    /**
     * span操作名
     */
    private final String operationName;

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

    

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String operationName;
        private String alias;
        private String kind;
        private SpanPropagation propagation;

        public Builder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        public Builder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder kind(String kind) {
            this.kind = kind;
            return this;
        }

        public Builder propagation(SpanPropagation propagation) {
            this.propagation = propagation;
            return this;
        }

        public SpanInfo build() {
            return new SpanInfo(
                NullSafe.of(operationName, ""),
                NullSafe.of(alias, ""),
                NullSafe.of(kind, Tags.SPAN_KIND_SERVER),
                NullSafe.of(propagation, SpanPropagation.SUPPORTS));
        }

    }

}
