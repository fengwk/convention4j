package fun.fengwk.convention4j.common.idgen;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class SimpleNamespaceIdGenerator<ID> implements NamespaceIdGenerator<ID> {

    private final ConcurrentMap<String, IdGenerator<ID>> registry = new ConcurrentHashMap<>();
    private final Function<String, IdGenerator<ID>> idGeneratorFactory;

    public SimpleNamespaceIdGenerator(Function<String, IdGenerator<ID>> idGeneratorFactory) {
        this.idGeneratorFactory = Objects.requireNonNull(idGeneratorFactory);
    }

    @Override
    public ID next(String namespace) {
        return getIdGenerator(namespace).next();
    }

    private IdGenerator<ID> getIdGenerator(String namespace) {
        return registry.computeIfAbsent(namespace, idGeneratorFactory);
    }

    @Override
    public void close(boolean releaseResource) throws Exception {
        for (Map.Entry<String, IdGenerator<ID>> entry : registry.entrySet()) {
            entry.getValue().close(releaseResource);
        }
    }

}
