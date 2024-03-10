package fun.fengwk.convention4j.tracer.util;

/**
 * @author fengwk
 */
public enum SpanPropagation {

    /**
     * 即使不存在父级span，也创建新的起点span
     */
    REQUIRED,

    /**
     * 仅当存在父级span时才创建新的span
     */
    SUPPORTS;

}
