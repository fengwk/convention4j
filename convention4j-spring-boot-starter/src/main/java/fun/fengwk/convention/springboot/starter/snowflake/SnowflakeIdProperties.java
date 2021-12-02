package fun.fengwk.convention.springboot.starter.snowflake;

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
    private Integer workerId;

    public Long getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(Long initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

}
