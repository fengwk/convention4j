package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycle;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;

/**
 * @author fengwk
 */
public class SimpleNamespaceIdGenerator<ID> extends AbstractLifeCycle implements NamespaceIdGenerator<ID> {

    private final ConcurrentMap<String, IdGenerator<ID>> registry = new ConcurrentHashMap<>();
    private final Function<String, IdGenerator<ID>> idGeneratorFactory;

    public SimpleNamespaceIdGenerator(Function<String, IdGenerator<ID>> idGeneratorFactory) {
        this.idGeneratorFactory = Objects.requireNonNull(idGeneratorFactory);
    }

    @Override
    public ID next(String namespace) {
        getLifeCycleRwLock().readLock().lock();
        try {
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            return getIdGenerator(namespace).next();

        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    private IdGenerator<ID> getIdGenerator(String namespace) {
        return registry.computeIfAbsent(namespace, ns -> {
            IdGenerator<ID> idGen = idGeneratorFactory.apply(ns);

            try {
                idGen.init();
                idGen.start();
            } catch (LifeCycleException ex) {
                throw new RuntimeLifeCycleException(ex);
            }

            return idGen;
        });
    }

    @Override
    protected void doInit() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStart() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStop() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doClose() throws LifeCycleException {
        for (LifeCycle lifeCycle : registry.values()) {
            lifeCycle.stop();
            lifeCycle.close();
        }
    }

    @Override
    protected void doFail() throws LifeCycleException {
        for (LifeCycle lifeCycle : registry.values()) {
            lifeCycle.stop();
            lifeCycle.close();
        }
    }

}
