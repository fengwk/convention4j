package fun.fengwk.convention4j.springboot.test.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.SimpleNamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.snowflakes.FixedWorkerIdClient;
import fun.fengwk.convention4j.common.idgen.snowflakes.SnowflakesIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
public class SnowflakeIdConfig {

    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator(
            @Value("${convention.snowflake-id.initial-timestamp:}") Long initialTimestamp) {
        return new SimpleNamespaceIdGenerator<>(
                ns -> new SnowflakesIdGenerator(
                        initialTimestamp == null ? System.currentTimeMillis() : initialTimestamp,
                        new FixedWorkerIdClient(0))
        );
    }

}
