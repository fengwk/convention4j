package fun.fengwk.convention4j.common.http.client;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class AsyncHttpSendResult extends AbstractHttpSendResult {

    private final BodyCollector bodyCollector;

    public AsyncHttpSendResult(BodyCollector bodyCollector) {
        this.bodyCollector = bodyCollector;
    }

    /**
     * body collector, 必须在入参中开启才会有对应的收集器返回, 否则返回null
     *
     * @return body 收集器 or null
     */
    public BodyCollector getBodyCollector() {
        return bodyCollector;
    }

}
