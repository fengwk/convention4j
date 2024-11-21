package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.example.ProducerSingleton;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fengwk
 */
@Slf4j
public class ProducerTest {


    public static void main(String[] args) throws ClientException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        String topic = "test";
        final Producer producer = ProducerSingleton.getInstance(topic);
        // Define your message body.
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
//        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
            // Set topic for the current message.
            .setTopic(topic)
            // Message secondary classifier of message besides topic.
//            .setTag(tag)
            // Key(s) of the message, another way to mark message besides message id.
            .setKeys("yourMessageKey-0e094a5f9d85")
            .setBody(body)
            .build();
        // Set individual thread pool for send callback.
        final CompletableFuture<SendReceipt> future = producer.sendAsync(message);
        ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();
        future.whenCompleteAsync((sendReceipt, throwable) -> {
            if (null != throwable) {
                log.error("Failed to send message", throwable);
                // Return early.
                return;
            }
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        }, sendCallbackExecutor);
        // Block to avoid exist of background threads.
        Thread.sleep(Long.MAX_VALUE);
        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // producer.close();
    }

}
