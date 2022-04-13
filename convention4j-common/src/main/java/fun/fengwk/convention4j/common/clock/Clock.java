package fun.fengwk.convention4j.common.clock;

/**
 * {@link Clock}是一个时钟接口，用于获取当前时间。
 * 
 * @author fengwk
 */
/**
 * 
 * @author fengwk
 */
public interface Clock {
    
    /**
     * 以毫秒为单位返回当前时间，虽然返回值的时间单位是毫秒，但值的粒度取决于底层实现。
     * 
     * @return
     */
    long currentTimeMillis();

    /**
     * 以微秒为单位返回当前时间，虽然返回值的时间单位是微秒，但值的粒度取决于底层实现。
     * 
     * @return
     */
    long currentTimeMicros();
    
}
