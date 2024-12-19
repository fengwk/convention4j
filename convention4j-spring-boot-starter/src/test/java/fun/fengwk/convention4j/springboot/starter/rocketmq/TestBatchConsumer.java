package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.RocketMQBatchMessageListener;
import fun.fengwk.convention4j.common.rocketmq.RocketMQUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
@Component
public class TestBatchConsumer {

    @RocketMQBatchMessageListener(topic = "TOPIC_TEST", consumerGroup = "CID_TEST_BATCH")
    public List<MessageView> consume(List<MessageView> messageViewList) {
        String rec = messageViewList.stream().map(RocketMQUtils::getStringBody).collect(Collectors.joining("/"));
        log.info("TestBatchConsumer receive: {}", rec);
        return messageViewList;
    }

}
