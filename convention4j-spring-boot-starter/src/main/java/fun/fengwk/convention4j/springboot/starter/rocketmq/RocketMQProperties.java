package fun.fengwk.convention4j.springboot.starter.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.rocketmq")
public class RocketMQProperties {

    /**
     * broker代理端口
     */
    private String endpoints;

    /**
     * 生产者配置
     */
    private ProducerConfig producer;

    /**
     * 消费者配置
     */
    private ConsumerConfig consumer;

    @Data
    public static class ProducerConfig {

        /**
         * 消息发布内部最大重试次数
         */
        private Integer maxAttempts;

    }

    @Data
    public static class ConsumerConfig {

        /**
         * 本地缓存的最大消息数
         */
        private Integer maxCacheMessageCount;

        /**
         * 本地缓存消息的最大字节数
         */
        private Integer maxCacheMessageSizeInBytes;

        /**
         * 消费者并行线程数
         */
        private Integer consumptionThreadCount;

    }

}
