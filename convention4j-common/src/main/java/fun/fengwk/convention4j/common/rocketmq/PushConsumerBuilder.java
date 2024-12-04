package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class PushConsumerBuilder extends AbstractConsumerBuilder<PushConsumer> {

    private MessageListener listener;
    private Integer maxCacheMessageCount;
    private Integer maxCacheMessageSizeInBytes;
    private Integer consumptionThreadCount;

    @Override
    protected PushConsumer doBuild(ClientConfiguration clientConfiguration,
                                   Map<String, FilterExpression> subscriptionMap) throws ClientException {
        org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder pcb = ClientServiceProviderHolder.get().newPushConsumerBuilder();
        pcb.setClientConfiguration(clientConfiguration);
        if (getConsumerGroup() != null) {
            pcb.setConsumerGroup(getConsumerGroup());
        }
        if (!subscriptionMap.isEmpty()) {
            pcb.setSubscriptionExpressions(subscriptionMap);
        }
        if (listener != null) {
            pcb.setMessageListener(new MessageListenerWrapper(listener));
        }
        if (maxCacheMessageCount != null) {
            pcb.setMaxCacheMessageCount(maxCacheMessageCount);
        }
        if (maxCacheMessageSizeInBytes != null) {
            pcb.setMaxCacheMessageSizeInBytes(maxCacheMessageSizeInBytes);
        }
        if (consumptionThreadCount != null) {
            pcb.setConsumptionThreadCount(consumptionThreadCount);
        }
        return pcb.build();
    }

}
