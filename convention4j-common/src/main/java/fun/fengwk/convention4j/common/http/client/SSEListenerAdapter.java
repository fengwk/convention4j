package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * @author fengwk
 */
public class SSEListenerAdapter extends AbstractStreamBodyListener<String> {

    private static final String DATA_KEY = "data:";
    private static final String EVENT_KEY = "event:";
    private static final String ID_KEY = "id:";
    private static final String RETRY_KEY = "retry:";
    private static final String COMMENT_KEY = ":";

    private final SSEListener sseListener;

    public SSEListenerAdapter(SSEListener sseListener) {
        this.sseListener = Objects.requireNonNull(sseListener, "sseListener must not be null");
    }

    @Override
    protected void onInit0(HttpResponse.ResponseInfo responseInfo) throws Exception {
        sseListener.onInit(responseInfo);
    }

    @Override
    protected void onReceive0(String chunk) throws Exception {
        if (chunk.startsWith(DATA_KEY)) {
            sseListener.onReceiveData(chunk.substring(DATA_KEY.length()));
        } else if (chunk.startsWith(EVENT_KEY)) {
            sseListener.onReceiveData(chunk.substring(EVENT_KEY.length()));
        } else if (chunk.startsWith(ID_KEY)) {
            sseListener.onReceiveData(chunk.substring(ID_KEY.length()));
        } else if (chunk.startsWith(RETRY_KEY)) {
            sseListener.onReceiveData(chunk.substring(RETRY_KEY.length()));
        } else if (chunk.startsWith(COMMENT_KEY)) {
            sseListener.onReceiveData(chunk.substring(COMMENT_KEY.length()));
        } else {
            sseListener.onReceiveOther(chunk);
        }
    }

    @Override
    protected void onComplete0() throws Exception {
        sseListener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) throws Exception {
        sseListener.onError(throwable);
    }

}
