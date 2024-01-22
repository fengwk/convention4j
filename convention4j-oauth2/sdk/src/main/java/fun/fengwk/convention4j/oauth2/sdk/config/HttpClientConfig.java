package fun.fengwk.convention4j.oauth2.sdk.config;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class HttpClientConfig {

    /**
     * 执行线程数
     */
    private int executeThreadCount = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * 执行缓冲队列大小
     */
    private int executeQueueSize = 10000;

    /**
     * TCP连接超时时间，单位毫秒
     */
    private long connectTimeoutMs = 1000 * 10L;

}
