package fun.fengwk.convention4j.springboot.starter.cache.mapper.provider;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.transaction.TransactionExecutor;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class MapperWriteTransactionSupport implements WriteTransactionSupport {

    private final TransactionExecutor transactionExecutor;

    @Override
    public <T> T transactionalWrite(Supplier<T> writer) {
        return transactionExecutor.executeWithRequired(writer::get);
    }

}
