package fun.fengwk.convention.springboot.starter.snowflake;

import fun.fengwk.commons.idgen.NamespaceIdGenerator;
import fun.fengwk.commons.idgen.SimpleNamespaceIdGenerator;
import fun.fengwk.commons.idgen.snowflakes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(SnowflakeIdProperties.class)
@ConditionalOnClass(SnowflakesIdGenerator.class)
@ConditionalOnProperty(prefix = "convention.snowflake-id", name = "initial-timestamp")
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class SnowflakeIdAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeIdAutoConfiguration.class);

    @ConditionalOnProperty(prefix = "convention.snowflake-id", name = "worker-id")
    @ConditionalOnMissingBean(WorkerIdClient.class)
    @Bean
    public WorkerIdClient fixedWorkerIdClient(SnowflakeIdProperties snowflakeIdProperties) {
        WorkerIdClient workerIdClient = new FixedWorkerIdClient(snowflakeIdProperties.getWorkerId());
        LOG.info("{} created", FixedWorkerIdClient.class.getSimpleName());
        return workerIdClient;
    }

    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(WorkerIdClient.class)
    @Bean
    public WorkerIdClient redisWorkerIdClient(StringRedisTemplate redisTemplate) {
        WorkerIdClient workerIdClient = new RedisWorkerIdClient(new RedisTemplateScriptExecutor(redisTemplate));
        LOG.info("{} created", RedisWorkerIdClient.class.getSimpleName());
        return workerIdClient;
    }

    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator(SnowflakeIdProperties snowflakeIdProperties,
                                                            WorkerIdClient workerIdClient) {
        NamespaceIdGenerator<Long> namespaceIdGenerator = new SimpleNamespaceIdGenerator<>(
                ns -> new SnowflakesIdGenerator(
                        snowflakeIdProperties.getInitialTimestamp(),
                        workerIdClient)
                );

        GlobalSnowflakeIdGenerator.setInstance(namespaceIdGenerator);

        LOG.info("NamespaceSnowflakesIdGenerator autoconfiguration successfully, workerId: {}, initialTimestamp: {} ", 
                snowflakeIdProperties.getWorkerId(), snowflakeIdProperties.getInitialTimestamp());

        return namespaceIdGenerator;
    }

}
