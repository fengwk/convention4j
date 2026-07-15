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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private volatile Runnable bodyCleanup;

    void setBody(T body) {
        this.body = body;
    }

    void setBodyCleanup(Runnable bodyCleanup) {
        this.bodyCleanup = bodyCleanup;
    }

    void cleanupBody() {
        Runnable cleanup = bodyCleanup;
        if (cleanup != null) {
            cleanup.run();
        }
    }

}
