package fun.fengwk.convention4j.springboot.test.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.SimpleNamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.snowflakes.FixedWorkerIdClient;
import fun.fengwk.convention4j.common.idgen.snowflakes.SnowflakesIdGenerator;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
public class SnowflakeIdTestConfig {

    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator(
            @Value("${convention.snowflake-id.initial-timestamp:}") Long initialTimestamp) throws LifeCycleException {
        SimpleNamespaceIdGenerator<Long> idGenerator = new SimpleNamespaceIdGenerator<>(
                ns -> new SnowflakesIdGenerator(
                        initialTimestamp == null ? System.currentTimeMillis() : initialTimestamp,
                        new FixedWorkerIdClient(0))
        );
        idGenerator.init();
        idGenerator.start();
        return idGenerator;
    }

}
