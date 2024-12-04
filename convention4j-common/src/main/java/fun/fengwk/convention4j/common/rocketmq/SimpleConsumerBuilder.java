package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;

import java.time.Duration;
import java.util.Map;

/**
 * {@link SimpleConsumer}构建者
 *
 * @author fengwk
 */
@Data
public class SimpleConsumerBuilder extends AbstractConsumerBuilder<SimpleConsumer> {

    private static final Duration DEFAULT_AWAIT_DURATION = Duration.ofSeconds(30);

    private Duration awaitDuration = DEFAULT_AWAIT_DURATION;

    @Override
    protected SimpleConsumer doBuild(ClientConfiguration clientConfiguration,
                                     Map<String, FilterExpression> subscriptionMap) throws ClientException {
        org.apache.rocketmq.client.apis.consumer.SimpleConsumerBuilder scb = ClientServiceProviderHolder.get().newSimpleConsumerBuilder();
        scb.setClientConfiguration(clientConfiguration);
        if (getConsumerGroup() != null) {
            scb.setConsumerGroup(getConsumerGroup());
        }
        if (!subscriptionMap.isEmpty()) {
            scb.setSubscriptionExpressions(subscriptionMap);
        }
        if (awaitDuration != null) {
            scb.setAwaitDuration(awaitDuration);
        }
        return scb.build();
    }

}
