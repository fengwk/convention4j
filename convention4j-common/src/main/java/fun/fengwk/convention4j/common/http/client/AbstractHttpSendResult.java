package fun.fengwk.convention4j.common.http.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
@Data
public class AbstractHttpSendResult extends AbstractHttpResponse {

    private Throwable error;

    public boolean hasError() {
        return error != null;
    }

}
