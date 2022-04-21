package fun.fengwk.convention4j.springboot.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class SnowflakeIdAutoConfigurationTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGenerator;
    
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
    
}
