package fun.fengwk.convention4j.common.idgen;

/**
 * {@link IdGenerator}对象能够不断生成下一个ID。
 *
 * @author fengwk
 */
public interface IdGenerator<ID> extends LifeCycle {

    /**
     * 生成下一个id。
     * 
     * @return
     * @throws ClosedException 如果在IdGenerator已关闭，将抛出该异常。
     */
    ID next() throws ClosedException;

}
