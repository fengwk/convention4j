#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import ${package}.core.model.Demo;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fengwk
 */
@Repository
public class TestDemoRepository extends AbstractTestRepository<Demo, Long> implements DemoRepository {

    private final AtomicLong idGen = new AtomicLong(1L);

    @Override
    protected Long getId(Demo demo) {
        return NullSafe.map(demo, Demo::getId);
    }

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public long generateId() {
        return idGen.getAndIncrement();
    }

    @Override
    public boolean add(Demo demo) {
        return doInsert(demo);
    }

    @Override
    public boolean removeById(long id) {
        return doDeleteById(id);
    }

    @Override
    public Page<Demo> page(PageQuery pageQuery) {
        return doPage(pageQuery, d -> true);
    }

}
