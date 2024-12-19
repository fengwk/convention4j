package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.common.rocketmq.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Slf4j
@Component
public class TestProducer {

    private final Producer producer;

    public TestProducer(Producer producer) {
        this.producer = producer;
    }

//    @ConventionSpan(value = "test_produce", propagation = SpanPropagation.REQUIRED)
    public void produce(String val) throws ClientException {
        MessageBuilder mb = new MessageBuilder();
        mb.setTopic("TOPIC_TEST");
        mb.setStringBody("hello: " + val);
        Message message = mb.build();
        SendReceipt sendReceipt = producer.send(message);
        log.info("produce result: {}", sendReceipt);
    }

}
