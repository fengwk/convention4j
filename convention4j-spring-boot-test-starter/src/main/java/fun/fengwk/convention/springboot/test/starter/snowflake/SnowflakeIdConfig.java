package fun.fengwk.convention.springboot.test.starter.snowflake;

import fun.fengwk.commons.idgen.NamespaceIdGenerator;
import fun.fengwk.commons.idgen.SimpleNamespaceIdGenerator;
import fun.fengwk.commons.idgen.snowflakes.FixedWorkerIdClient;
import fun.fengwk.commons.idgen.snowflakes.SnowflakesIdGenerator;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 */
public class SnowflakeIdConfig {

    @Bean
    public NamespaceIdGenerator<Long> snowflakesIdGenerator() {
        return new SimpleNamespaceIdGenerator<>(
                ns -> new SnowflakesIdGenerator(
                        System.currentTimeMillis(),
                        new FixedWorkerIdClient(0))
        );
    }

}
