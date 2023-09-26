#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.domain.service;

import ${package}.core.CoreTestApplication;
import ${package}.core.domain.model.FooBO;
import ${package}.core.domain.model.FooCreateBO;
import ${package}.core.domain.repo.FooRepository;
import ${package}.share.constant.FooStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreTestApplication.class)
public class CreateFooServiceTest {

    @Autowired
    private CreateFooService createFooService;
    @Autowired
    private FooRepository fooRepository;

    @Test
    public void test() {
        FooCreateBO createBO = new FooCreateBO();
        createBO.setName("foo");
        createBO.setStatus(FooStatus.NORMAL);
        FooBO created = createFooService.create(createBO);
        assert created != null;
        FooBO found = fooRepository.getById(created.getId());
        assert created.equals(found);
    }

}
