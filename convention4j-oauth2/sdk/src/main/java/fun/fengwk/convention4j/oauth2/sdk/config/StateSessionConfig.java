package fun.fengwk.convention4j.oauth2.sdk.config;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class StateSessionConfig {

    /**
     * 最大的令牌存储时间，单位秒，默认1小时
     */
    private int maxStoreSeconds = 60 * 60;

}
