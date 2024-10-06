//package fun.fengwk.convention4j.common.idgen.snowflakes;//package fun.fengwk.commons.idgen.snowflakes;
//
//import org.junit.jupiter.api.Test;
//import redis.clients.jedis.*;
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
//        System.out.println(getLua("redis_applyIdleWorkerId.lua"));
//    }
//
//    @Test
//    public void test2() throws IOException {
//        String clientId = getClientId();
//        String lockTime = "60";
//        try (Pool<Jedis> pool = getPool()) {
//            try (Jedis jedis = pool.getResource()) {
//                Object res = jedis.eval(
//                        getLua("redis_applyIdleWorkerId.lua"),
//                        Collections.singletonList(WORKER_HASH_KEY),
//                        Arrays.asList(clientId, lockTime, "0", "1024"));
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
//                        getLua("redis_renewWorkerId.lua"),
//                        Collections.singletonList(WORKER_HASH_KEY),
//                        Arrays.asList("5e9c6361e853481e88a3762ab94a3349", "60", "0"));
//                System.out.println(res);
//            }
//        }
//    }
//
//    private Pool<Jedis> getPool() {
//        String host = System.getenv("VPS_REDIS_HOST");
//        String port = System.getenv("VPS_REDIS_PORT");
//        String password = System.getenv("VPS_REDIS_PASSWORD");
//        HostAndPort hostAndPort = new HostAndPort(host, Integer.parseInt(port));
//        return new JedisPool(hostAndPort, DefaultJedisClientConfig.builder().password(password).build());
//
////        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
////        jedisPoolConfig.setMaxTotal(1);
////        jedisPoolConfig.setMaxIdle(1);
////        jedisPoolConfig.setMinIdle(1);
////
////        Set<String> sentinels = new HashSet<>();
////        sentinels.add("redis.fengwk.fun:26379");
////        sentinels.add("redis.fengwk.fun:26380");
////        sentinels.add("redis.fengwk.fun:26381");
////        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels,
////                jedisPoolConfig, 1000 * 3, "a123");
////
////        return jedisSentinelPool;
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
