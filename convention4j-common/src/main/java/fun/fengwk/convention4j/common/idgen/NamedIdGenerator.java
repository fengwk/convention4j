package fun.fengwk.convention4j.common.idgen;

import java.util.Objects;

/**
 * 为IdGenerator自定义名称。
 *
 * @author fengwk
 */
public class NamedIdGenerator<ID> implements IdGenerator<ID> {

    private final IdGenerator<ID> idGenerator;
    private final String name;
    
    public NamedIdGenerator(IdGenerator<ID> idGenerator, String name) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public ID next() {
        return idGenerator.next();
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public void close(boolean releaseResource) throws Exception {
        idGenerator.close(releaseResource);
    }

}
