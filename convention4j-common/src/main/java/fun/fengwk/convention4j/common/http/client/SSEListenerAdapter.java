package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.lang.StringUtils;

import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * @author fengwk
 */
public class SSEListenerAdapter extends AbstractStreamBodyListener<String> {

    private static final String COLON = ":";
    private static final String BLANK = " ";
    private static final String LF = "\n";
    private static final String EVENT_KEY = "event";
    private static final String DATA_KEY = "data";
    private static final String ID_KEY = "id";
    private static final String RETRY_KEY = "retry";

    private final SSEListener sseListener;

    private volatile SSEEvent buffer;

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
        sseListener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) {
        sseListener.onError(throwable);
    }

    private void processSSE(String line) {
        if (line.isBlank()) {
            dispatchEvent();
        } else if (line.startsWith(COLON)) {
            String comment = line.substring(COLON.length());
            sseListener.onReceiveComment(comment);
        } else {
            int colonIdx = line.indexOf(COLON);
            String key, value;
            if (colonIdx != -1) {
                key = line.substring(0, colonIdx);
                value = line.substring(colonIdx + 1);
                value = trimFirstBlank(value);
            } else {
                key = line;
                value = StringUtils.EMPTY;
            }

            switch (key) {
                case EVENT_KEY -> {
                    SSEEvent buffer = getOrCreateBuffer();
                    buffer.setEvent(value);
                }
                case DATA_KEY -> {
                    SSEEvent buffer = getOrCreateBuffer();
                    String data = buffer.getData();
                    if (data == null) {
                        data = value + LF;
                    } else {
                        data += value + LF;
                    }
                    buffer.setData(data);
                }
                case ID_KEY -> {
                    SSEEvent buffer = getOrCreateBuffer();
                    buffer.setId(value);
                }
                case RETRY_KEY -> {
                    try {
                        long retry = Long.parseLong(value);
                        SSEEvent buffer = getOrCreateBuffer();
                        buffer.setRetry(retry);
                    } catch (NumberFormatException ignore) {}
                }
            }
        }
    }

    private void dispatchEvent() {
        if (buffer == null) {
            return;
        }
        sseListener.onReceiveEvent(buffer);
        this.buffer = null;
    }

    private String trimFirstBlank(String value) {
        if (value.startsWith(BLANK)) {
            return value.substring(BLANK.length());
        }
        return value;
    }

    private SSEEvent getOrCreateBuffer() {
        if (buffer == null) {
            this.buffer = new SSEEvent();
        }
        return buffer;
    }

}
