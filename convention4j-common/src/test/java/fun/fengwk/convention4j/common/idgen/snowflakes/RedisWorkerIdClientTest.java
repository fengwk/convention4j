package fun.fengwk.convention4j.common.idgen.snowflakes;//package fun.fengwk.commons.idgen.snowflakes;
//
//import org.junit.Test;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.JedisSentinelPool;
//import redis.clients.jedis.util.Pool;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author fengwk
// */
//public class RedisWorkerIdClientTest {
//
//    @Test
//    public void test1() throws InterruptedException {
//        Set<Long> workerIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
//        AtomicInteger repeat = new AtomicInteger();
//        int count = 1024;
//        Pool<Jedis> pool = getPool();
//        RedisScriptExecutor scriptExecutor = new JedisPoolScriptExecutor(pool);
//
//        List<RedisWorkerIdClient> clients = new CopyOnWriteArrayList<>();
//        for (int i = 0; i < count; i++) {
//            RedisWorkerIdClient client = new RedisWorkerIdClient(scriptExecutor);
//            clients.add(client);
//        }
//
//        for (int i = 0; i < count; i++) {
//            int idx = i;
//            new Thread(() -> {
//                long workerId = 0;
//                workerId = clients.get(idx).get();
//                if (!workerIdSet.add(workerId)) {
//                    repeat.incrementAndGet();
//                }
//                System.out.println(workerId);
//                try {
//                    clients.get(idx).close(false);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//
//        for (RedisWorkerIdClient client : clients) {
//            client.waitClosed();
//        }
//        pool.close();
//        System.out.println(workerIdSet.size());
//        assert repeat.get() == 0;
//        assert workerIdSet.size() == count;
//        System.out.println(workerIdSet);
//    }
//
//    private static Pool<Jedis> getPool() {
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
//    }
//
//}
