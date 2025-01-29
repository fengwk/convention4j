package fun.fengwk.convention4j.springboot.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.idgen.snowflakes.RedisTemplateExecutor;
import fun.fengwk.convention4j.common.idgen.snowflakes.RedisWorkerIdClient;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author fengwk
 */
@SpringBootTest(classes = TestApplication.class)
public class SnowflakeIdAutoConfigurationTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGenerator;
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Test
    public void test1() {
        long id1 = idGenerator.next(SnowflakeIdAutoConfigurationTest.class);
        long id2 = idGenerator.next(SnowflakeIdAutoConfigurationTest.class);
        assert id2 > id1;
    }

    @Test
    public void test2() {
        long id1 = GlobalSnowflakeIdGenerator.next(SnowflakeIdAutoConfigurationTest.class);
        long id2 = GlobalSnowflakeIdGenerator.next(SnowflakeIdAutoConfigurationTest.class);
        assert id2 > id1;
    }

    @Test
    public void test3() throws InterruptedException, LifeCycleException {
        Set<Long> workerIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AtomicInteger repeat = new AtomicInteger();
        int count = 1024;

        CountDownLatch cdl = new CountDownLatch(1024);

        List<RedisWorkerIdClient> clients = new CopyOnWriteArrayList<>();
        for (int i = 0; i < count; i++) {
            RedisWorkerIdClient client = new RedisWorkerIdClient("default2", new RedisTemplateExecutor(redisTemplate)) {
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
    
}
