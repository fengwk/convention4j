package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * @author fengwk
 */
public class SSEListenerAdapter extends AbstractStreamBodyListener<String> {

    private final SSEListener sseListener;
    private final SSEDecoder sseDecoder = new SSEDecoder();

    public SSEListenerAdapter(SSEListener sseListener) {
        this.sseListener = Objects.requireNonNull(sseListener, "sseListener must not be null");
    }

    @Override
    protected void onInit0(HttpResponse.ResponseInfo responseInfo) {
        sseListener.onInit(responseInfo);
    }

    @Override
    protected void onReceive0(String line) {
        sseListener.onReceive(line);
        processSSE(line);
    }

    @Override
    protected void onComplete0() {
        for (SSEEvent sseEvent : sseDecoder.finish()) {
            sseListener.onReceiveEvent(sseEvent);
        }
        sseListener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) {
        sseListener.onError(throwable);
    }

    private void processSSE(String line) {
        sseDecoder.appendLine(line);
        SSEEvent sseEvent;
        while ((sseEvent = sseDecoder.nextEvent()) != null) {
            sseListener.onReceiveEvent(sseEvent);
        }
    }

}
