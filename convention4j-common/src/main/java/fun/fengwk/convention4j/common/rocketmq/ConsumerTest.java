package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class ConsumerTest {

    public static void main(String[] args) throws ClientException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        // Credential provider is optional for client configuration.
        String accessKey = "yourAccessKey";
        String secretKey = "yourSecretKey";
        SessionCredentialsProvider sessionCredentialsProvider =
            new StaticSessionCredentialsProvider(accessKey, secretKey);

        String endpoints = "vps-rocketmq-broker:8081";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(endpoints)
            // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
            // client configuration to solve the problem please if SSL is not essential.
             .enableSsl(false)
//            .setCredentialProvider(sessionCredentialsProvider)
            .build();
        String consumerGroup = "testGroup";
        Duration awaitDuration = Duration.ofSeconds(30);
        String tag = "yourMessageTagA";
        String topic = "test";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // In most case, you don't need to create too many consumers, singleton pattern is recommended.
        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            // Set the consumer group name.
            .setConsumerGroup(consumerGroup)
            // set await duration for long-polling.
            .setAwaitDuration(awaitDuration)
            // Set the subscription for the consumer.
//            .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
            .setSubscriptionExpressions(Collections.singletonMap(topic, FilterExpression.SUB_ALL))
            .build();
        // Max message num for each long polling.
        int maxMessageNum = 16;
        // Set message invisible duration after it is received.
        Duration invisibleDuration = Duration.ofSeconds(15);
        // Set individual thread pool for receive callback.
        ExecutorService receiveCallbackExecutor = Executors.newCachedThreadPool();
        // Set individual thread pool for ack callback.
        ExecutorService ackCallbackExecutor = Executors.newCachedThreadPool();
        // Receive message.
        do {
            final CompletableFuture<List<MessageView>> future0 = consumer.receiveAsync(maxMessageNum,
                invisibleDuration);
            future0.whenCompleteAsync(((messages, throwable) -> {
                if (null != throwable) {
                    log.error("Failed to receive message from remote", throwable);
                    // Return early.
                    return;
                }
                log.info("Received {} message(s)", messages.size());
                // Using messageView as key rather than message id because message id may be duplicated.
                final Map<MessageView, CompletableFuture<Void>> map =
                    messages.stream().collect(Collectors.toMap(message -> message, consumer::ackAsync));
                for (Map.Entry<MessageView, CompletableFuture<Void>> entry : map.entrySet()) {
                    final MessageId messageId = entry.getKey().getMessageId();
                    final CompletableFuture<Void> future = entry.getValue();
                    future.whenCompleteAsync((v, t) -> {
                        if (null != t) {
                            log.error("Message is failed to be acknowledged, messageId={}", messageId, t);
                            // Return early.
                            return;
                        }
                        log.info("Message is acknowledged successfully, messageId={}", messageId);
                    }, ackCallbackExecutor);
                }

            }), receiveCallbackExecutor);
        } while (true);
        // Close the simple consumer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        // consumer.close();
    }

}
