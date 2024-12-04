//package fun.fengwk.convention4j.common.rocketmq;
//
//import lombok.Data;
//import org.apache.rocketmq.client.apis.ClientConfiguration;
//import org.apache.rocketmq.client.apis.ClientException;
//import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
//import org.apache.rocketmq.client.apis.consumer.FilterExpression;
//import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
//import org.apache.rocketmq.client.apis.message.Message;
//import org.apache.rocketmq.client.apis.message.MessageView;
//import org.apache.rocketmq.client.apis.producer.Producer;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
///**
// * @author fengwk
// */
//public class RocketMqTest {
//
//    private final ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
//        .setEndpoints("vps-rocketmq-broker:8081")
//        .build();
//
//    @Test
//    public void testConsumer() throws ClientException, IOException, ExecutionException, InterruptedException {
//        SimpleConsumerBuilder scb = new SimpleConsumerBuilder();
//        scb.setConsumerGroup("CID_test");
//        scb.addSubscription(new Subscription("test", FilterExpression.SUB_ALL));
//        SimpleConsumer consumer = scb.build(clientConfiguration);
//
//        CompletableFuture<List<MessageView>> cf = consumer.receiveAsync(16, Duration.ofSeconds(15));
//        List<MessageView> messageViews = cf.get();
//        for (MessageView messageView : messageViews) {
//            System.out.println(mv2Str(messageView));
//            consumer.ack(messageView);
//        }
//
//        consumer.close();
//    }
//
//    @Test
//    public void testProducer() throws ClientException, IOException {
//        ProducerBuilder pb = new ProducerBuilder();
//        Producer producer = pb.build(clientConfiguration);
//
//        MessageBuilder mb = new MessageBuilder();
//        mb.setTopic("test");
//        mb.setBody("hello");
//        Message message = mb.build();
//        producer.send(message);
//
//        producer.close();
//    }
//
//    @Test
//    public void testRocketMQConsumerManager() throws ClientException, InterruptedException {
//        RocketMQConsumerManager rocketMQConsumerManager = new RocketMQConsumerManager(clientConfiguration);
//        rocketMQConsumerManager.registerIfNecessary(new TestListener());
//        Thread.sleep(1000 * 1000L);
//        rocketMQConsumerManager.close();
//    }
//
//    @Test
//    public void testProducer2() throws ClientException, IOException {
//        ProducerBuilder pb = new ProducerBuilder();
//        Producer producer = pb.build(clientConfiguration);
//
//        SimpleData sd = new SimpleData();
//        sd.setName("test_name");
//
//        MessageBuilder mb = new MessageBuilder();
//        mb.setTopic("test");
//        mb.setObjectBody(sd);
//        Message message = mb.build();
//        producer.send(message);
//
//        producer.close();
//    }
//
//    private String mv2Str(MessageView messageView) {
//        ByteBuffer buf = messageView.getBody();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        while (buf.hasRemaining()) {
//            out.write(buf.get());
//        }
//        return out.toString(StandardCharsets.UTF_8);
//    }
//
//    @Data
//    static class SimpleData {
//
//        private String name;
//
//    }
//
//    static class TestListener {
//
////        @RocketMQMessageListener(topic = "test", consumerGroup = "CID_test")
////        public ConsumeResult handle1(MessageView mv) {
////            System.out.println("handle1, mv: " + mv);
////            return ConsumeResult.SUCCESS;
////        }
//
////        @RocketMQMessageListener(topic = "test", consumerGroup = "CID_test")
////        public ConsumeResult handle2(String message) {
////            System.out.println("handle2, message: " + message);
////            return ConsumeResult.SUCCESS;
////        }
//
//        @RocketMQMessageListener(topic = "test", consumerGroup = "CID_test")
//        public ConsumeResult handle3(SimpleData simpleData) {
//            System.out.println("handle3, simpleData: " + simpleData);
//            return ConsumeResult.SUCCESS;
//        }
//
//    }
//
//}
