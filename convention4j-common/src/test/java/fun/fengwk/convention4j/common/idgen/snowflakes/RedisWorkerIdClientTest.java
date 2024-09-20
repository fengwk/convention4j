//package fun.fengwk.convention4j.common.idgen.snowflakes;//package fun.fengwk.commons.idgen.snowflakes;
//
//import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
//import org.junit.Test;
//import redis.clients.jedis.*;
//import redis.clients.jedis.util.Pool;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author fengwk
// */
//public class RedisWorkerIdClientTest {
//
//    @Test
//    public void test1() throws InterruptedException, LifeCycleException {
//        Set<Long> workerIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
//        AtomicInteger repeat = new AtomicInteger();
//        int count = 1024;
//        Pool<Jedis> pool = getPool();
//
//        CountDownLatch cdl = new CountDownLatch(1024);
//
//        List<RedisWorkerIdClient> clients = new CopyOnWriteArrayList<>();
//        for (int i = 0; i < count; i++) {
//            RedisWorkerIdClient client = new RedisWorkerIdClient("default", new JedisPoolScriptExecutor(pool)) {
//                @Override
//                protected void onStopped() {
//                    super.onStopped();
//                    cdl.countDown();
//                }
//            };
//            client.init();
//            client.start();
//            clients.add(client);
//        }
//
//        for (int i = 0; i < count; i++) {
//            int idx = i;
//            new Thread(() -> {
//                long workerId = clients.get(idx).get();
//                if (!workerIdSet.add(workerId)) {
//                    repeat.incrementAndGet();
//                }
//                System.out.println(workerId);
//                try {
//                    clients.get(idx).stop();
//                } catch (LifeCycleException ex) {
//                    ex.printStackTrace();
//                }
//            }).start();
//        }
//
//        cdl.await();
//        for (RedisWorkerIdClient client : clients) {
//            client.close();
//        }
//
//        System.out.println(workerIdSet.size());
//        assert repeat.get() == 0;
//        assert workerIdSet.size() == count;
//        System.out.println(workerIdSet);
//    }
//
//    private static Pool<Jedis> getPool() {
//        String host = System.getenv("VPS_REDIS_HOST");
//        String port = System.getenv("VPS_REDIS_PORT");
//        String password = System.getenv("VPS_REDIS_PASSWORD");
//        HostAndPort hostAndPort = new HostAndPort(host, Integer.parseInt(port));
//        return new JedisPool(hostAndPort, DefaultJedisClientConfig.builder().password(password).build());
//
////        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
////        jedisPoolConfig.setMaxTotal(100);
////        jedisPoolConfig.setMaxIdle(50);
////        jedisPoolConfig.setMinIdle(50);
////        jedisPoolConfig.setMaxWaitMillis(-1);
////
////        Set<String> sentinels = new HashSet<String>();
////        sentinels.add("redis.fengwk.fun:26379");
////        sentinels.add("redis.fengwk.fun:26380");
////        sentinels.add("redis.fengwk.fun:26381");
////        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels,
////                jedisPoolConfig, 1000 * 3, "a123");
////
////        return jedisSentinelPool;
//    }
//
//}
