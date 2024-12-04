package fun.fengwk.convention4j.common.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fengwk
 */
@Data
public abstract class AbstractConsumerBuilder<C> {

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * 订阅集合
     */
    private final Set<Subscription> subscriptions = new HashSet<>();

    /**
     * 添加一个订阅
     *
     * @param subscription subscription
     */
    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    /**
     * 构建消费者
     *
     * @param clientConfiguration 客户端参数配置
     * @return 消费者
     * @throws ClientException ClientException
     */
    public C build(ClientConfiguration clientConfiguration) throws ClientException {
        Map<String, FilterExpression> subscriptionMap = new HashMap<>();
        for (Subscription subscription : subscriptions) {
            subscriptionMap.put(subscription.getTopic(), subscription.getFilterExpression());
        }
        return doBuild(clientConfiguration, subscriptionMap);
    }

    protected abstract C doBuild(ClientConfiguration clientConfiguration,
                                 Map<String, FilterExpression> subscriptionMap) throws ClientException;

}
