package fun.fengwk.convention4j.springboot.test.starter.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * @author fengwk
 */
public class EmbeddedRedisServerConfiguration {

    static final String REDIS_SERVER_BEAN_NAME = "redisServer";

    @Bean(name = REDIS_SERVER_BEAN_NAME, initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer(@Value("${spring.redis.port:6379}") int port) throws IOException {
        return new RedisServer(port);
    }

}
