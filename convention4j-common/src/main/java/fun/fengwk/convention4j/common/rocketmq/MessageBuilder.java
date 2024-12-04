package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import lombok.Data;
import org.apache.rocketmq.client.apis.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class MessageBuilder {

    private static final List<MessageBuilderProcessor> PROCESSORS = LazyServiceLoader
        .loadServiceIgnoreLoadFailed(MessageBuilderProcessor.class);

    private String topic;
    private byte[] body;
    private String tag;
    private final List<String> keys = new ArrayList<>();
    private String messageGroup;
    private Long deliveryTimestamp;
    private final Map<String, String> properties = new HashMap<>();

    public void setStringBody(String body) {
        setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public void setObjectBody(Object body) {
        setStringBody(JsonUtils.toJson(body));
    }

    public void addKey(String key) {
        keys.add(key);
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public Message build() {
        org.apache.rocketmq.client.apis.message.MessageBuilder mb = ClientServiceProviderHolder.get().newMessageBuilder();
        if (topic != null) {
            mb.setTopic(topic);
        }
        if (body != null) {
            mb.setBody(body);
        }
        if (tag != null) {
            mb.setTag(tag);
        }
        if (!keys.isEmpty()) {
            mb.setKeys(keys.toArray(new String[0]));
        }
        if (messageGroup != null) {
            mb.setMessageGroup(messageGroup);
        }
        if (deliveryTimestamp != null) {
            mb.setDeliveryTimestamp(deliveryTimestamp);
        }
        if (!properties.isEmpty()) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                mb.addProperty(entry.getKey(), entry.getValue());
            }
        }

        postProcess(mb);

        return mb.build();
    }

    private void postProcess(org.apache.rocketmq.client.apis.message.MessageBuilder mb) {
        for (MessageBuilderProcessor processor : PROCESSORS) {
            processor.postProcess(mb);
        }
    }

}
