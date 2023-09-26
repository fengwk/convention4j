#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.domain.repo;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import ${package}.core.domain.model.FooBO;
import org.springframework.stereotype.Repository;

/**
 * @author fengwk
 */
@Repository
public class TestFooRepository extends AbstractTestRepository<FooBO, String> implements FooRepository {

    private final NamespaceIdGenerator<Long> idGenerator;

    public TestFooRepository(NamespaceIdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    protected String getId(FooBO fooBO) {
        return fooBO.getId();
    }

    @Override
    public String allocateId() {
        return String.valueOf(idGenerator.next(getClass()));
    }

}
