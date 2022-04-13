package fun.fengwk.convention4j.springboot.starter.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author fengwk
 */
@ConfigurationProperties("convention.snowflake-id")
public class SnowflakeIdProperties {
    
    /**
     * 雪花算法初始时间戳
     */
    private Long initialTimestamp;
    
    /**
     * 节点编号[0, 1024)
     */
    private Long workerId;

    /**
     * 自动获取工人id策略：
     * redis: redis自增。
     * zk:    zk有序节点。
     */
    private String autoWorkIdStrategy;

    public Long getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(Long initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getAutoWorkIdStrategy() {
        return autoWorkIdStrategy;
    }

    public void setAutoWorkIdStrategy(String autoWorkIdStrategy) {
        this.autoWorkIdStrategy = autoWorkIdStrategy;
    }

}
