package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class RocketMQUtils {

    private RocketMQUtils() {}

    /**
     * 获取字符串body
     *
     * @param messageView messageView
     * @return 字符串body
     */
    public static String getStringBody(MessageView messageView) {
        if (messageView == null) {
            return null;
        }
        ByteBuffer buf = messageView.getBody();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while (buf.hasRemaining()) {
                out.write(buf.get());
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

    /**
     * 获取对象body
     *
     * @param messageView messageView
     * @param clazz 对象类型
     * @return 对象body
     */
    public static <T> T getObjectBody(MessageView messageView, Class<T> clazz) {
        String stringBody = getStringBody(messageView);
        if (stringBody == null) {
            return null;
        }
        return JsonUtils.fromJson(stringBody, clazz);
    }

    /**
     * 获取对象body
     *
     * @param messageView messageView
     * @param type 对象类型
     * @return 对象body
     */
    public static <T> T getObjectBody(MessageView messageView, Type type) {
        String stringBody = getStringBody(messageView);
        if (stringBody == null) {
            return null;
        }
        return JsonUtils.fromJson(stringBody, type);
    }

}
