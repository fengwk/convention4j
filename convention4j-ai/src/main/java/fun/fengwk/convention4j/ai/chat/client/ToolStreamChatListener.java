package fun.fengwk.convention4j.ai.chat.client;

import fun.fengwk.convention4j.ai.chat.response.ChatResponse;
import lombok.Getter;

import java.util.LinkedList;

/**
 * 对用户缓冲区进行包装，用户无需监听到function内容
 *
 * @author fengwk
 */
public class ToolStreamChatListener implements StreamChatListener {

    /**
     * 如果是增量消息，且空消息缓冲区为空，无法判断当前消息是否是function，因此需要先暂存
     */
    private final LinkedList<ChatResponse> emptyDeltaResponseBuffer = new LinkedList<>();

    private final StreamChatListener delegate;
    @Getter
    private volatile boolean function;

    public ToolStreamChatListener(StreamChatListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onReceive(ChatResponse chatResponse) {
        if (ChatUtils.isEmptyDelta(chatResponse)) {
            emptyDeltaResponseBuffer.offer(chatResponse);
        } else {
            if (function || ChatUtils.isToolCalls(chatResponse)) {
                function = true;
            } else {
                consumeLeftBuffer();
                delegate.onReceive(chatResponse);
            }
        }
    }

    @Override
    public void onComplete(ChatCompletionsResponse response) {
        if (!function) {
            consumeLeftBuffer();
            delegate.onComplete(response);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (!function) {
            consumeLeftBuffer();
            delegate.onError(throwable);
        }
    }

    private void consumeLeftBuffer() {
        while (!emptyDeltaResponseBuffer.isEmpty()) {
            delegate.onReceive(emptyDeltaResponseBuffer.poll());
        }
    }

}
