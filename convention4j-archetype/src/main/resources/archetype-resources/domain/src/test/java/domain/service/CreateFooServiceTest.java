#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.service;

import ${package}.domain.DomainTestApplication;
import ${package}.domain.model.FooBO;
import ${package}.domain.model.FooCreateBO;
import ${package}.domain.repo.FooRepository;
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
@SpringBootTest(classes = DomainTestApplication.class)
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
