package fun.fengwk.convention4j.common.idgen.snowflakes;//package fun.fengwk.commons.idgen.snowflakes;
//
//import org.junit.Test;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.JedisSentinelPool;
//import redis.clients.jedis.util.Pool;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.*;
//
///**
// * @author fengwk
// */
//public class JedisLuaTest {
//
//    private static final String WORKER_HASH_KEY = "workers";
//
//    @Test
//    public void test1() throws IOException {
//        System.out.println(getLua("redis_getIdleWorkerId.lua"));
//    }
//
//    @Test
//    public void test2() throws IOException {
//        String clientId = getClientId();
//        String lockTime = "60";
//        try (Pool<Jedis> pool = getPool()) {
//            try (Jedis jedis = pool.getResource()) {
//                Object res = jedis.eval(
//                        getLua("redis_getIdleWorkerId.lua"),
//                        Collections.singletonList(WORKER_HASH_KEY),
//                        Arrays.asList(clientId, lockTime));
//                System.out.println("clientId: " + clientId);
//                System.out.println(res);
//            }
//        }
//    }
//
//    @Test
//    public void test3() throws IOException {
//        try (Pool<Jedis> pool = getPool()) {
//            try (Jedis jedis = pool.getResource()) {
//                Object res = jedis.eval(
//                        getLua("redis_keepaliveWorkerId.lua"),
//                        Collections.singletonList(WORKER_HASH_KEY),
//                        Arrays.asList("5e9c6361e853481e88a3762ab94a3349", "60", "0"));
//                System.out.println(res);
//            }
//        }
//    }
//
//    private Pool<Jedis> getPool() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(1);
//        jedisPoolConfig.setMaxIdle(1);
//        jedisPoolConfig.setMinIdle(1);
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
//    private String getLua(String classpath) throws IOException {
//        InputStream input = ClassLoader.getSystemResourceAsStream(classpath);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = input.read(buf)) != -1) {
//            out.write(buf, 0, len);
//        }
//        return out.toString("utf-8");
//    }
//
//    private String getClientId() {
//        return UUID.randomUUID().toString().replace("-", "");
//    }
//
//}
