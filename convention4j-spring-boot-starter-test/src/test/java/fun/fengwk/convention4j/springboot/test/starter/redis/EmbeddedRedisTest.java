package fun.fengwk.convention4j.springboot.test.starter.redis;

import org.junit.Test;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * @author fengwk
 */
public class EmbeddedRedisTest {

    @Test
    public void test() throws IOException {
        RedisServer redisServer = new RedisServer(6379);
        redisServer.start();
        redisServer.stop();
    }

}
