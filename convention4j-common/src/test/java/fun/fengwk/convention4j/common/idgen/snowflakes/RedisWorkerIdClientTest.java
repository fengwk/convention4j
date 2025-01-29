package fun.fengwk.convention4j.common.idgen.snowflakes;//package fun.fengwk.commons.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.Pool;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fengwk
 */
public class RedisWorkerIdClientTest {

    private static final int PORT = 6379;

    private static RedisServer redisServer;

    @BeforeAll
    public static void before() throws IOException {
        redisServer = new RedisServer(PORT);
        redisServer.start();
    }

    @AfterAll
    public static void destroy() {
        redisServer.stop();
    }

    @Test
    public void test1() throws InterruptedException, LifeCycleException {
        Set<Long> workerIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AtomicInteger repeat = new AtomicInteger();
        int count = 1024;
        Pool<Jedis> pool = getPool();

        CountDownLatch cdl = new CountDownLatch(1024);

        List<RedisWorkerIdClient> clients = new CopyOnWriteArrayList<>();
        for (int i = 0; i < count; i++) {
            RedisWorkerIdClient client = new RedisWorkerIdClient("default", new JedisPoolExecutor(pool)) {
                @Override
                protected void onStopped() {
                    super.onStopped();
                    cdl.countDown();
                }
            };
            client.init();
            client.start();
            clients.add(client);
        }

        for (int i = 0; i < count; i++) {
            int idx = i;
            new Thread(() -> {
                long workerId = clients.get(idx).get();
                if (!workerIdSet.add(workerId)) {
                    repeat.incrementAndGet();
                }
                System.out.println(workerId);
                try {
                    clients.get(idx).stop();
                } catch (LifeCycleException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }

        cdl.await();
        for (RedisWorkerIdClient client : clients) {
            client.close();
        }

        System.out.println(workerIdSet.size());
        assert repeat.get() == 0;
        assert workerIdSet.size() == count;
        System.out.println(workerIdSet);
    }

    private static Pool<Jedis> getPool() {
        HostAndPort hostAndPort = new HostAndPort("127.0.0.1", PORT);
        return new JedisPool(hostAndPort, DefaultJedisClientConfig.builder().build());

//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(100);
//        jedisPoolConfig.setMaxIdle(50);
//        jedisPoolConfig.setMinIdle(50);
//        jedisPoolConfig.setMaxWaitMillis(-1);
//
//        Set<String> sentinels = new HashSet<String>();
//        sentinels.add("redis.fengwk.fun:26379");
//        sentinels.add("redis.fengwk.fun:26380");
//        sentinels.add("redis.fengwk.fun:26381");
//        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels,
//                jedisPoolConfig, 1000 * 3, "a123");
//
//        return jedisSentinelPool;
    }

}
