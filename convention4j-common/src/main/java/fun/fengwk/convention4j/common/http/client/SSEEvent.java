package fun.fengwk.convention4j.common.http.client;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class SSEEvent {

    private String event;
    private String data;
    private String id;
    private Long retry;

}
