package fun.fengwk.convention.springboot.starter.snowflake;

import fun.fengwk.commons.idgen.NamespaceIdGenerator;
import fun.fengwk.commons.idgen.SimpleNamespaceIdGenerator;
import fun.fengwk.commons.idgen.SnowflakesIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(SnowflakeIdProperties.class)
@ConditionalOnClass(SnowflakesIdGenerator.class)
@ConditionalOnProperty(prefix = "convention.snowflake-id", name = { "initial-timestamp", "worker-id" })
@Configuration(proxyBeanMethods = false)
public class SnowflakeIdAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeIdAutoConfiguration.class);
    
    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator(SnowflakeIdProperties snowflakeIdProperties) {
        NamespaceIdGenerator<Long> namespaceIdGenerator = new SimpleNamespaceIdGenerator<>(
                ns -> new SnowflakesIdGenerator(
                        snowflakeIdProperties.getInitialTimestamp(), 
                        snowflakeIdProperties.getWorkerId())
                );

        GlobalSnowflakeIdGenerator.setInstance(namespaceIdGenerator);

        LOG.info("NamespaceSnowflakesIdGenerator autoconfiguration successfully, workerId: {}, initialTimestamp: {} ", 
                snowflakeIdProperties.getWorkerId(), snowflakeIdProperties.getInitialTimestamp());

        return namespaceIdGenerator;
    }

}
