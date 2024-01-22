package fun.fengwk.convention4j.springboot.starter.transaction;

import fun.fengwk.convention4j.common.function.Func0;
import fun.fengwk.convention4j.common.function.Func0T1;
import fun.fengwk.convention4j.common.function.VoidFunc0;
import fun.fengwk.convention4j.common.function.VoidFunc0T1;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务管理器
 * @author fengwk
 */
public class TransactionExecutor {

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void executeWithRequired(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <R> R executeWithRequired(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#SUPPORTS}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void executeWithSupports(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#SUPPORTS}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public <R> R executeWithSupports(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#MANDATORY}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void executeWithMandatory(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#MANDATORY}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public <R> R executeWithMandatory(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRES_NEW}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeWithRequireNew(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRES_NEW}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <R> R executeWithRequireNew(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NOT_SUPPORTED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void executeWithNotSupported(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NOT_SUPPORTED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public <R> R executeWithNotSupported(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NEVER}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NEVER)
    public void executeWithNever(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NEVER}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NEVER)
    public <R> R executeWithNever(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NESTED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NESTED)
    public void executeWithNested(VoidFunc0 executor) {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NESTED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NESTED)
    public <R> R executeWithNested(Func0<R> executor) {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T extends Throwable> void executeWithRequiredWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <R, T extends Throwable> R executeWithRequiredWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#SUPPORTS}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public <T extends Throwable> void executeWithSupportsWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#SUPPORTS}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public <R, T extends Throwable> R executeWithSupportsWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#MANDATORY}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public <T extends Throwable> void executeWithMandatoryWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#MANDATORY}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public <R, T extends Throwable> R executeWithMandatoryWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRES_NEW}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends Throwable> void executeWithRequireNewWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#REQUIRES_NEW}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <R, T extends Throwable> R executeWithRequireNewWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NOT_SUPPORTED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public <T extends Throwable> void executeWithNotSupportedWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NOT_SUPPORTED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public <R, T extends Throwable> R executeWithNotSupportedWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NEVER}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NEVER)
    public <T extends Throwable> void executeWithNeverWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NEVER}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NEVER)
    public <R, T extends Throwable> R executeWithNeverWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NESTED}
     * @param executor 执行器
     */
    @Transactional(propagation = Propagation.NESTED)
    public <T extends Throwable> void executeWithNestedWithThrowable(VoidFunc0T1<T> executor) throws T {
        executor.apply();
    }

    /**
     * 事务性执行，传播方式为{@link Propagation#NESTED}
     * @param executor 执行器
     * @param <R> 返回值类型
     * @return 返回值
     */
    @Transactional(propagation = Propagation.NESTED)
    public <R, T extends Throwable> R executeWithNestedWithThrowable(Func0T1<R, T> executor) throws T {
        return executor.apply();
    }

}
