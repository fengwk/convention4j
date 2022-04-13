package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.idgen.IdGenerator;

import java.util.Objects;

/**
 * 雪花id生成器，为了简化起见，该生成器将datacenterId和machineId统一成workerId，取值范围[0, 1024)。
 * <pre>
 * |-- 1位符号位 --|-- 41位时间戳 --|-- 10位节点号 --|-- 12位序列号 --|
 * </pre>
 * 
 * @author fengwk
 */
public class SnowflakesIdGenerator implements IdGenerator<Long> {

    /** 符号位掩膜 **/
    private static final long SIGN_MASK = -1L >>> 1;
    /** 时间戳占用位数 **/
    private final static long BIT_TIMESTAMP = 41L;
    /** 节点占用位数 **/
    private final static long BIT_NODE = 10L;
    /** 序列号占用位数 **/
    private final static long BIT_SEQUENCE = 12L;
    /** 时间戳最大值 **/
    private final static long MAX_TIMESTAMP = (1L << BIT_TIMESTAMP) - 1L;
    /** 节点id最大值 **/
    private final static long MAX_NODE_ID = (1L << BIT_NODE) - 1L;
    /** 序列号最大值 **/
    private final static long MAX_SEQUENCE = (1L << BIT_SEQUENCE) - 1L;
    /** 时间戳左移位数 **/
    private final static long TO_LEFT_TIMESTAMP = BIT_NODE + BIT_SEQUENCE;
    /** 节点id左移位数  **/
    private final static long TO_LEFT_NODE_ID = BIT_SEQUENCE;
    /** 初始时间戳 **/
    private final long initialTimestamp;
    /** 工作节点编号 **/
    private final WorkerIdClient workerIdClient;
    /** 上一次时间戳  **/
    private long lastTimestamp = -1;
    /** 序列号  **/
    private long sequence = -1;
    
    /**
     * 
     * @param initialTimestamp 初始化时间戳,一旦设置不允许改变
     * @param workerIdClient not null, 工作节点编号客户端
     */
    public SnowflakesIdGenerator(long initialTimestamp, WorkerIdClient workerIdClient) {
        if (initialTimestamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException("Initial timestamp cannot be greater than current time millis.");
        }

        this.initialTimestamp = initialTimestamp;
        this.workerIdClient = Objects.requireNonNull(workerIdClient);
    }

    @Override
    public synchronized Long next() {
        long cms = System.currentTimeMillis();
        if (cms > lastTimestamp) {
            lastTimestamp = cms;
            sequence = 0;
        } else if (++sequence > MAX_SEQUENCE) {
            // 自旋直到更大的毫秒来临
            while ((cms = System.currentTimeMillis()) <= lastTimestamp) {
                Thread.yield();
            }
            lastTimestamp = cms;
            sequence = 0;
        }
        return compound(lastTimestamp, sequence);
    }
    
    public long compound(long lt, long ns) {
        long offset = lt - initialTimestamp;
        if (offset > MAX_TIMESTAMP) {
            throw new IllegalStateException("Snow id is invalid because current timestamp greater than " + MAX_TIMESTAMP + ".");
        }

        return SIGN_MASK & (offset << TO_LEFT_TIMESTAMP | workerIdClient.get() << TO_LEFT_NODE_ID | ns);
    }

    @Override
    public String toString() {
        return String.format("%s-[%d, %d]",
                SnowflakesIdGenerator.class.getSimpleName(), workerIdClient.tryGet(), initialTimestamp);
    }

    @Override
    public void close(boolean releaseResource) throws Exception {
        if (releaseResource) {
            workerIdClient.close();
        }
    }

}
