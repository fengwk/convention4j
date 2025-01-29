package fun.fengwk.convention4j.springboot.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.snowflakes.*;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger log = LoggerFactory.getLogger(SnowflakeIdAutoConfiguration.class);

//    @ConditionalOnProperty(prefix = "convention.snowflake-id", name = "worker-id")
//    @ConditionalOnMissingBean
//    @Bean
//    public WorkerIdClient fixedWorkerIdClient(SnowflakeIdProperties snowflakeIdProperties) {
//        WorkerIdClient workerIdClient = new FixedWorkerIdClient(snowflakeIdProperties.getWorkerId());
//        log.info("{} created", FixedWorkerIdClient.class.getSimpleName());
//        return workerIdClient;
//    }
//
//    @ConditionalOnClass(StringRedisTemplate.class)
//    @ConditionalOnBean(StringRedisTemplate.class)
//    @ConditionalOnMissingBean
//    @Bean
//    public WorkerIdClient redisWorkerIdClient(@Value("${spring.application.name}") String appName,
//                                              StringRedisTemplate redisTemplate) throws LifeCycleException {
//        WorkerIdClient workerIdClient = new RedisWorkerIdClient(appName, new RedisTemplateScriptExecutor(redisTemplate));
//        workerIdClient.init();
//        workerIdClient.start();
//        log.info("{} running", RedisWorkerIdClient.class.getSimpleName());
//        return workerIdClient;
//    }

    @ConditionalOnMissingBean
    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator(SnowflakeIdProperties snowflakeIdProperties,
                                                            WorkerIdClient workerIdClient) {
        NamespaceIdGenerator<Long> namespaceIdGenerator = new SimpleNamespaceIdGeneratorBean<>(
                ns -> new SnowflakesIdGenerator(
                        snowflakeIdProperties.getInitialTimestamp(),
                        workerIdClient)
                );

        GlobalSnowflakeIdGenerator.setInstance(namespaceIdGenerator);

        log.info("{} autoconfiguration successfully, workerId: {}, initialTimestamp: {}",
                SimpleNamespaceIdGeneratorBean.class.getSimpleName(),
                snowflakeIdProperties.getWorkerId(),
                snowflakeIdProperties.getInitialTimestamp());

        return namespaceIdGenerator;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "convention.snowflake-id", name = "worker-id")
    public static class FixedWorkerIdClientConfig {

        @ConditionalOnMissingBean
        @Bean
        public WorkerIdClient fixedWorkerIdClient(SnowflakeIdProperties snowflakeIdProperties) {
            WorkerIdClient workerIdClient = new FixedWorkerIdClient(snowflakeIdProperties.getWorkerId());
            log.info("{} created", FixedWorkerIdClient.class.getSimpleName());
            return workerIdClient;
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    public static class RedisWorkerIdClientConfig {

        @ConditionalOnMissingBean
        @Bean
        public WorkerIdClient redisWorkerIdClient(@Value("${spring.application.name}") String appName,
            StringRedisTemplate redisTemplate) throws LifeCycleException {
            WorkerIdClient workerIdClient = new RedisWorkerIdClient(appName, new RedisTemplateExecutor(redisTemplate));
            workerIdClient.init();
            workerIdClient.start();
            log.info("{} running", RedisWorkerIdClient.class.getSimpleName());
            return workerIdClient;
        }

    }

}
