package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.RocketMQMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Slf4j
@Component
public class TestConsumer {

    @RocketMQMessageListener(topic = "TOPIC_TEST", consumerGroup = "CID_TEST")
    public void consume(String message) {
        log.info("TestConsumer receive: {}", message);
    }

}
