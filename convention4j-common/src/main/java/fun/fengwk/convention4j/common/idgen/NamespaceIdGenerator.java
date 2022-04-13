package fun.fengwk.convention4j.common.idgen;

/**
 * 使用命名空间相互隔离的ID生成器。
 *
 * @author fengwk
 */
public interface NamespaceIdGenerator<ID> extends LifeCycle {

    /**
     * 生成指定命名空间的下一个id。
     *
     * @param namespace
     * @return
     * @throws ClosedException 如果当前NamespaceIdGenerator已经被关闭，将抛出该异常。
     */
    ID next(String namespace) throws ClosedException;

    /**
     * 使用类的全路径作为命名空间生产下一个id。
     *
     * @param namespace
     * @return
     * @throws ClosedException 如果当前NamespaceIdGenerator已经被关闭，将抛出该异常。
     */
    default ID next(Class<?> namespace) throws ClosedException {
        return next(namespace.getName());
    }

}

