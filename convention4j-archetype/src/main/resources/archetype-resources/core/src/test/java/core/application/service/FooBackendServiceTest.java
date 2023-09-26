#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.application.service;

import ${package}.core.CoreTestApplication;
import ${package}.share.constant.FooStatus;
import ${package}.share.model.FooCreateDTO;
import ${package}.share.model.FooDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestApplication.class)
public class FooBackendServiceTest {

    @Autowired
    private FooBackendService fooBackendService;

    @Test
    public void test() {
        FooCreateDTO createDTO = new FooCreateDTO();
        createDTO.setName("foo");
        createDTO.setStatus(FooStatus.NORMAL.name());
        String fooId = fooBackendService.createFoo(createDTO);
        assert StringUtils.isNotEmpty(fooId);
        FooDTO found = fooBackendService.getByFooId(fooId);
        assert Objects.equals(fooId, found.getId());
        assert Objects.equals(createDTO.getName(), found.getName());
        assert Objects.equals(createDTO.getStatus(), found.getStatus());
    }

}
