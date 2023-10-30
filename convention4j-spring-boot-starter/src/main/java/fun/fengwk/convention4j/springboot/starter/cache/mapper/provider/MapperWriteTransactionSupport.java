package fun.fengwk.convention4j.springboot.starter.cache.mapper.provider;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import lombok.AllArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class MapperWriteTransactionSupport implements WriteTransactionSupport {

    private final TransactionTemplate transactionTemplate;

    @Override
    public <T> T transactionalWrite(Supplier<T> writer) {
        return transactionTemplate.execute(status -> {
            try {
                return writer.get();
            } catch (Exception ex) {
                status.setRollbackOnly();
                throw ex;
            }
        });
    }

}
