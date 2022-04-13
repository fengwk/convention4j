package fun.fengwk.convention4j.common.idgen;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * {@link FilterableIdGenerator}为{@link IdGenerator}添加ID过滤能力。
 * 
 * @author fengwk
 */
public class FilterableIdGenerator<ID> implements IdGenerator<ID> {
    
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
    public ID next() {
        ID id;
        do {
            id = idGenerator.next();
        } while (!filter.test(id));
        return id;
    }

    @Override
    public void close(boolean releaseResource) throws Exception {
        idGenerator.close(releaseResource);
    }

}
