package fun.fengwk.convention4j.springboot.test.starter.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.embedded.RedisServer;

/**
 * @author fengwk
 */
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = RedisTestApplication.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisServer redisServer;

    @Test
    public void test() {
        String name = "fengwk";
        redisTemplate.opsForValue().set("name", name);
        assert redisTemplate.opsForValue().get("name").equals(name);

        // 由于junit框架可能在容器执行完销毁前就结束
        // 因此手动执行关闭，防止stop未触发
        redisServer.stop();
    }

}
