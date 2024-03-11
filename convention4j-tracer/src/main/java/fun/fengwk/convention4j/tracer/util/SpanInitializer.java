package fun.fengwk.convention4j.tracer.util;

import io.opentracing.Span;

/**
 * span初始化器
 *
 * @author fengwk
 */
public interface SpanInitializer {

    /**
     * 初始化根span，作为新起点的span构建后会调用该方法进行初始化
     *
     * @param span span
     */
    default void initializeRootSpan(Span span) {}

    /**
     * 初始化span，每个span构建后都会调用该方法进行初始化
     *
     * @param span span
     */
    default void initializeSpan(Span span) {}

}
