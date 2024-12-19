package fun.fengwk.convention4j.springboot.starter.rocketmq;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.apache.rocketmq.client.apis.ClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fengwk
 */
@SpringBootTest(classes = TestApplication.class)
public class RocketMQTest {

    @Autowired
    private TestProducer testProducer;

    @Test
    public void test() throws ClientException, InterruptedException {
        testProducer.produce("1");
        testProducer.produce("2");
        testProducer.produce("3");
        Thread.sleep(1000L);
    }

}
