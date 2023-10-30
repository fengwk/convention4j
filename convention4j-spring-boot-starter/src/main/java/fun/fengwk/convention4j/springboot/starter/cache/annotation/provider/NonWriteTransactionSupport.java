package fun.fengwk.convention4j.springboot.starter.cache.annotation.provider;

import java.util.function.Supplier;

/**
 * 无事务写支持，可以提供更好的并发度，但在W1 R R_CACHE W2 W1_CLEAR W2_CLEAR执行序列发生的时候R_CACHE将无法成功失效造成数据不一致。
 * @author fengwk
 */
public class NonWriteTransactionSupport implements WriteTransactionSupport {

    @Override
    public <T> T transactionalWrite(Supplier<T> writer) {
        return writer.get();
    }

}
