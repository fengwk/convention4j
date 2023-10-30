package fun.fengwk.convention4j.springboot.starter.cache.annotation.provider;

import java.util.function.Supplier;

/**
 * @author fengwk
 */
public interface WriteTransactionSupport {

    /**
     * 支持事务性写入。
     *
     * @param writer 写入器。
     * @return writer提供的返回值。
     * @param <T> writer提供的返回值类型。
     */
    <T> T transactionalWrite(Supplier<T> writer);

}
