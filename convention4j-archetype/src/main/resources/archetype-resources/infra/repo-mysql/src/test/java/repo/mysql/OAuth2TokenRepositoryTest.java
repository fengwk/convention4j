#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql;

import ${package}.domain.model.FooBO;
import ${package}.domain.model.FooCreateBO;
import ${package}.domain.model.FooFactory;
import ${package}.domain.repo.FooRepository;
import ${package}.share.constant.FooStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RepoMysqlTestApplication.class)
public class OAuth2TokenRepositoryTest {

    @Autowired
    private FooFactory fooFactory;
    @Autowired
    private FooRepository fooRepository;

    @Transactional
    @Test
    public void testCrud() {
        FooCreateBO createBO = new FooCreateBO();
        createBO.setName("foo");
        createBO.setStatus(FooStatus.NORMAL);
        FooBO fooBO = fooFactory.create(createBO);
        assert fooRepository.insert(fooBO);
        assert fooRepository.getById(fooBO.getId()) != null;
    }

}
