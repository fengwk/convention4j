#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import ${package}.core.CoreTestApplication;
import ${package}.share.model.DemoCreateDTO;
import ${package}.share.model.DemoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author fengwk
 */
@SpringBootTest(classes = CoreTestApplication.class)
public class DemoServiceTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void test() {
        DemoCreateDTO createDTO = new DemoCreateDTO();
        createDTO.setName("test");
        DemoDTO demoDTO = demoService.createDemo(createDTO);
        assertNotNull(demoDTO);
        assertEquals(createDTO.getName(), demoDTO.getName());

        PageQuery pageQuery = new PageQuery(1, 10);
        Page<DemoDTO> page = demoService.pageDemo(pageQuery);
        assertEquals(page.getTotalCount(), 1L);
        assertEquals(page.getResults().get(0), demoDTO);

        demoService.removeDemo(demoDTO.getId());
        page = demoService.pageDemo(pageQuery);
        assertEquals(page.getTotalCount(), 0L);
    }

}
