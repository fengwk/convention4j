package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQConsumerManager;
import fun.fengwk.convention4j.common.rocketmq.AbstractRocketMQMessageListenerConfig;
import fun.fengwk.convention4j.common.rocketmq.RocketMQBatchMessageListenerConfig;
import fun.fengwk.convention4j.common.rocketmq.RocketMQMessageListenerConfig;
import fun.fengwk.convention4j.common.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.context.ApplicationListener;

import java.util.*;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMQConsumerRefresher implements ApplicationListener<RocketMQPropertiesChangedEvent> {

    private List<RocketMQMessageListenerConfig> prevConsumerConfigs;
    private List<RocketMQMessageListenerConfig> curConsumerConfigs;
    private List<RocketMQBatchMessageListenerConfig> prevBatchConsumerConfigs;
    private List<RocketMQBatchMessageListenerConfig> curBatchConsumerConfigs;

    private final AbstractRocketMQConsumerManager rocketMQConsumerManager;

    public RocketMQConsumerRefresher(AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        this.rocketMQConsumerManager = Objects.requireNonNull(rocketMQConsumerManager);
    }

    @Override
    public synchronized void onApplicationEvent(RocketMQPropertiesChangedEvent event) {
        this.prevConsumerConfigs = curConsumerConfigs;
        this.curConsumerConfigs = event.getRocketMQProperties().getConsumers();
        this.prevBatchConsumerConfigs = curBatchConsumerConfigs;
        this.curBatchConsumerConfigs = event.getRocketMQProperties().getBatchConsumers();

        if (!equalsList(prevConsumerConfigs, curConsumerConfigs)) {
            Set<Pair<String, String>> consumerGroupTopicSet = new HashSet<>(
                collectConsumerGroupTopicSet(prevConsumerConfigs));
            consumerGroupTopicSet.addAll(collectConsumerGroupTopicSet(curConsumerConfigs));
            for (Pair<String, String> consumerGroupTopic : consumerGroupTopicSet) {
                try {
                    rocketMQConsumerManager.refreshConsumer(consumerGroupTopic.getKey(), consumerGroupTopic.getValue());
                } catch (ClientException ex) {
                    log.error("refresh consumer error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                }
            }
        }

        if (!equalsList(prevBatchConsumerConfigs, curBatchConsumerConfigs)) {
            Set<Pair<String, String>> consumerGroupTopicSet = new HashSet<>(
                collectConsumerGroupTopicSet(prevBatchConsumerConfigs));
            consumerGroupTopicSet.addAll(collectConsumerGroupTopicSet(curBatchConsumerConfigs));
            for (Pair<String, String> consumerGroupTopic : consumerGroupTopicSet) {
                try {
                    rocketMQConsumerManager.refreshBatchConsumer(consumerGroupTopic.getKey(), consumerGroupTopic.getValue());
                } catch (ClientException ex) {
                    log.error("refresh batch consumer error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                }
            }
        }
    }

    private Set<Pair<String, String>> collectConsumerGroupTopicSet(
        List<? extends AbstractRocketMQMessageListenerConfig> configList) {
        if (configList == null) {
            return Collections.emptySet();
        }

        Set<Pair<String, String>> consumerGroupTopicSet = new HashSet<>();
        for (AbstractRocketMQMessageListenerConfig config : configList) {
            Pair<String, String> consumerGroupTopic = Pair.of(config.getConsumerGroup(), config.getTopic());
            consumerGroupTopicSet.add(consumerGroupTopic);
        }
        return consumerGroupTopicSet;
    }

    private <T> boolean equalsList(List<T> l1, List<T> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if (l1 == null || l2 == null) {
            return false;
        }
        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            if (!Objects.equals(l1.get(i), l2.get(i))) {
                return false;
            }
        }
        return true;
    }

}
