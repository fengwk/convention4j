package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fengwk
 */
public class CC {

    public static void main(String[] args) throws ClientException, ExecutionException, InterruptedException {

        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints("vps-rocketmq-broker:8081")
            .build();

        ClientServiceProvider provider = ClientServiceProvider.loadService();
//        provider.newPushConsumerBuilder()

        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            .setConsumerGroup("CID_test")
            .setAwaitDuration(Duration.ofSeconds(30))
            .setSubscriptionExpressions(Collections.singletonMap("test", FilterExpression.SUB_ALL))
            .build();
        CompletableFuture<List<MessageView>> cf = consumer.receiveAsync(16, Duration.ofSeconds(15));
        List<MessageView> messageViews = cf.get();
        for (MessageView messageView : messageViews) {
            ByteBuffer buf = messageView.getBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (buf.hasRemaining()) {
                out.write(buf.get());
            }
            String s = out.toString(StandardCharsets.UTF_8);
            System.out.println(s);
            consumer.ack(messageView);
        }
    }

}
