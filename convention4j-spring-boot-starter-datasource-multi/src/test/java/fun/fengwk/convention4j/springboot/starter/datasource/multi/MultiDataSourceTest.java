package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1.Ds1DO;
import fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1.Ds1Manager;
import fun.fengwk.convention4j.springboot.starter.datasource.multi.ds2.Ds2DO;
import fun.fengwk.convention4j.springboot.starter.datasource.multi.ds2.Ds2Manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fengwk
 */
@SpringBootTest(classes = TestApplication.class)
public class MultiDataSourceTest {

    @Autowired
    private Ds1Manager ds1Manager;
    @Autowired
    private Ds2Manager ds2Manager;

    @Test
    public void test() {
        String ds1Name = "ds1Name";
        String ds2Name = "ds2Name";
        Ds1DO ds1DO = ds1Manager.insertAndQuery(ds1Name);
        Ds2DO ds2DO = ds2Manager.insertAndQuery(ds2Name);
        Assertions.assertNotNull(ds1DO);
        Assertions.assertNotNull(ds2DO);
        Assertions.assertEquals(ds1Name, ds1DO.getName());
        Assertions.assertEquals(ds2Name, ds2DO.getName());
    }

}
