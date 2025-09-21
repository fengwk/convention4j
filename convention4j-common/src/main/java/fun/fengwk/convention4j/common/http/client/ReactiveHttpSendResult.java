package fun.fengwk.convention4j.common.http.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ReactiveHttpSendResult<T> extends AbstractHttpResponse {

    @Getter
    private volatile T body;

    void setBody(T body) {
        this.body = body;
    }

}
