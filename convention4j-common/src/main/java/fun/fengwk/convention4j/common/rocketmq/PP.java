package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;

import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class PP {

    public static void main(String[] args) throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints("vps-rocketmq-broker:8081")
            .build();
        ProducerBuilder builder = provider.newProducerBuilder()
            .setClientConfiguration(clientConfiguration);
        Producer producer = builder.build();

        final Message message = provider.newMessageBuilder()
            .setTopic("test")
            .setBody("hello".getBytes(StandardCharsets.UTF_8))
            .build();
        SendReceipt sr = producer.send(message);
        System.out.println(sr);
    }

}
