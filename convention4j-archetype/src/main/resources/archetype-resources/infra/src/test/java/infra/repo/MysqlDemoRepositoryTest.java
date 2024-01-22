#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infra.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import ${package}.core.model.Demo;
import ${package}.infra.InfraTestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InfraTestApplication.class)
public class MysqlDemoRepositoryTest {

    @Autowired
    private MysqlDemoRepository mysqlDemoRepository;

    @Transactional
    @Test
    public void test() {
        mysqlDemoRepository.init();

        Demo demo = new Demo();
        demo.setId(mysqlDemoRepository.generateId());
        demo.setName("test");
        LocalDateTime date = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        demo.setCreateTime(date);
        demo.setUpdateTime(date);
        assertTrue(mysqlDemoRepository.add(demo));

        PageQuery pageQuery = new PageQuery(1, 10);
        Page<Demo> page = mysqlDemoRepository.page(pageQuery);
        assertEquals(page.getTotalCount(), 1L);
        assertEquals(page.getResults().get(0), demo);

        assertTrue(mysqlDemoRepository.removeById(demo.getId()));

        page = mysqlDemoRepository.page(pageQuery);
        assertEquals(page.getTotalCount(), 0L);
    }

}
