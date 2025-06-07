package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.*;
import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import fun.fengwk.convention4j.common.util.NullSafe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;

import java.util.*;

/**
 * @author fengwk
 */
@Slf4j
public class RocketMQConsumerLifecycle implements ApplicationListener<RocketMQPropertiesChangedEvent>, SmartLifecycle {

    private volatile boolean running;
    private List<RocketMQMessageListenerConfig> curConsumerConfigs;
    private List<RocketMQBatchMessageListenerConfig> curBatchConsumerConfigs;

    private final AbstractRocketMQConsumerManager rocketMQConsumerManager;

    public RocketMQConsumerLifecycle(AbstractRocketMQConsumerManager rocketMQConsumerManager) {
        this.rocketMQConsumerManager = Objects.requireNonNull(rocketMQConsumerManager);
    }

    @Override
    public int getPhase() {
        // 与WebServer同一批次下线
        return WebServerGracefulShutdownLifecycle.SMART_LIFECYCLE_PHASE;
    }

    @Override
    public void onApplicationEvent(RocketMQPropertiesChangedEvent event) {
        onRocketMQPropertiesChangedEvent(event);
    }

    @Override
    public synchronized void start() {
        running = true;
        try {
            log.info("rocket mq consumers starting...");
            rocketMQConsumerManager.start();
            log.info("rocket mq consumers started successfully");
        } catch (ClientException ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

    // 生命周期的stop早于bean的销毁，以此来做到优雅停机
    @Override
    public synchronized void stop() {
        running = false;
        try {
            log.info("rocket mq consumers closing...");
            rocketMQConsumerManager.close();
            log.info("rocket mq consumers closed");
        } catch (Exception ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    // 配置变更后刷新消费者
    private synchronized void onRocketMQPropertiesChangedEvent(RocketMQPropertiesChangedEvent event) {
        List<RocketMQMessageListenerConfig> prevConsumerConfigs = curConsumerConfigs;
        this.curConsumerConfigs = NullSafe.map2List(event.getRocketMQProperties().getConsumers(),
            RocketMQMessageListenerConfig::copy);
        List<RocketMQBatchMessageListenerConfig> prevBatchConsumerConfigs = curBatchConsumerConfigs;
        this.curBatchConsumerConfigs = NullSafe.map2List(event.getRocketMQProperties().getBatchConsumers(),
            RocketMQBatchMessageListenerConfig::copy);

        // 还没有执行过start则无需刷新
        if (!running) {
            return;
        }

        if (!equalsList(prevConsumerConfigs, curConsumerConfigs)) {
            Set<ConsumerGroupTopic> consumerGroupTopicSet = new HashSet<>(
                collectConsumerGroupTopicSet(prevConsumerConfigs));
            consumerGroupTopicSet.addAll(collectConsumerGroupTopicSet(curConsumerConfigs));
            for (ConsumerGroupTopic consumerGroupTopic : consumerGroupTopicSet) {
                try {
                    rocketMQConsumerManager.refreshConsumer(consumerGroupTopic);
                    log.info("rocket mq consumer refreshed successfully, consumerGroupTopic: {}", consumerGroupTopic);
                } catch (IllegalArgumentException ex) {
                    log.warn("refresh consumer arguments error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                } catch (ClientException ex) {
                    log.error("refresh consumer error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                }
            }
        }

        if (!equalsList(prevBatchConsumerConfigs, curBatchConsumerConfigs)) {
            Set<ConsumerGroupTopic> consumerGroupTopicSet = new HashSet<>(
                collectConsumerGroupTopicSet(prevBatchConsumerConfigs));
            consumerGroupTopicSet.addAll(collectConsumerGroupTopicSet(curBatchConsumerConfigs));
            for (ConsumerGroupTopic consumerGroupTopic : consumerGroupTopicSet) {
                try {
                    rocketMQConsumerManager.refreshBatchConsumer(consumerGroupTopic);
                    log.info("rocket mq batch consumer refreshed successfully, consumerGroupTopic: {}", consumerGroupTopic);
                } catch (IllegalArgumentException ex) {
                    log.warn("refresh consumer arguments error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                } catch (ClientException ex) {
                    log.error("refresh batch consumer error, consumerGroupTopic: {}", consumerGroupTopic, ex);
                }
            }
        }
    }

    private Set<ConsumerGroupTopic> collectConsumerGroupTopicSet(
        List<? extends AbstractRocketMQMessageListenerConfig> configList) {
        if (configList == null) {
            return Collections.emptySet();
        }

        Set<ConsumerGroupTopic> consumerGroupTopicSet = new HashSet<>();
        for (AbstractRocketMQMessageListenerConfig config : configList) {
            consumerGroupTopicSet.add(new ConsumerGroupTopic(config.getConsumerGroup(), config.getTopic()));
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
