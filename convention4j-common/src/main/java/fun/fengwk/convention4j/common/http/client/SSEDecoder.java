package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fengwk
 */
@Slf4j
public class SSEDecoder {

    private static final String COLON = ":";
    private static final String BLANK = " ";
    private static final String LF = "\n";
    private static final String EVENT_KEY = "event";
    private static final String DATA_KEY = "data";
    private static final String ID_KEY = "id";
    private static final String RETRY_KEY = "retry";

    private SSEEvent buffer;
    private final LinkedList<SSEEvent> bufferQueue = new LinkedList<>();
    private boolean finished = false;

    public synchronized SSEEvent nextEvent() {
        if (finished) {
            throw new IllegalStateException(String.format("%s is finished", getClass().getSimpleName()));
        }
        return bufferQueue.poll();
    }

    public synchronized List<SSEEvent> finish() {
        if (finished) {
            throw new IllegalStateException(String.format("%s is finished", getClass().getSimpleName()));
        }
        this.finished = true;
        dispatchEvent();
        List<SSEEvent> ret = new ArrayList<>(bufferQueue);
        bufferQueue.clear();
        return ret;
    }

    public synchronized void appendLine(String line) {
        if (finished) {
            throw new IllegalStateException(String.format("%s is finished", getClass().getSimpleName()));
        }
        if (line.isBlank()) {
            dispatchEvent();
        } else if (line.startsWith(COLON)) {
            String comment = line.substring(COLON.length());
            // 根据规范注释行可以被忽略
            log.debug("ignore comment line, comment: {}", comment);
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
                        buffer.setData(value);
                    } else {
                        buffer.setData(data + LF + value);
                    }
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
        bufferQueue.offer(buffer);
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
