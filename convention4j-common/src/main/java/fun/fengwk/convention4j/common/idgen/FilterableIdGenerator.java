package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * {@link FilterableIdGenerator}为{@link IdGenerator}添加ID过滤能力。
 * 
 * @author fengwk
 */
public class FilterableIdGenerator<ID> extends AbstractIdGenerator<ID> {

    private final IdGenerator<ID> idGenerator;
    private final Predicate<ID> filter;

    /**
     * 只有filter测试为true的ID才回被当前生成器返回。
     * 
     * @param idGenerator
     * @param filter
     */
    public FilterableIdGenerator(IdGenerator<ID> idGenerator, Predicate<ID> filter) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.filter = Objects.requireNonNull(filter);
    }

    @Override
    protected ID doNext() {
        ID id;
        do {
            id = idGenerator.next();
        } while (!filter.test(id));
        return id;
    }

    @Override
    protected void doInit() throws LifeCycleException {
        idGenerator.init();
    }

    @Override
    protected void doStart() throws LifeCycleException {
        idGenerator.start();
    }

    @Override
    protected void doStop() throws LifeCycleException {
        idGenerator.stop();
    }

    @Override
    protected void doClose() throws LifeCycleException {
        idGenerator.close();
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

}
