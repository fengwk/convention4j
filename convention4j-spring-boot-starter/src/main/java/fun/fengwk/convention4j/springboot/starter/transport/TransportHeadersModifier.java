package fun.fengwk.convention4j.springboot.starter.transport;

/**
 * 操作{@link TransportHeaders}
 *
 * @author fengwk
 */
public interface TransportHeadersModifier {

    /**
     * 修改{@link TransportHeaders}
     *
     * @param transportHeaders TransportHeaders
     */
    void modify(TransportHeaders transportHeaders);

}
