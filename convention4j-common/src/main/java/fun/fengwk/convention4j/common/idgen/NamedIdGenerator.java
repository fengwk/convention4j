package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;

import java.util.Objects;

/**
 * 为IdGenerator自定义名称。
 *
 * @author fengwk
 */
public class NamedIdGenerator<ID> extends AbstractIdGenerator<ID> {

    private final IdGenerator<ID> idGenerator;
    private final String name;

    /**
     *
     * @param idGenerator not null
     * @param name not null
     */
    public NamedIdGenerator(IdGenerator<ID> idGenerator, String name) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    protected ID doNext() {
        return idGenerator.next();
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

    @Override
    public String toString() {
        return name;
    }

}
