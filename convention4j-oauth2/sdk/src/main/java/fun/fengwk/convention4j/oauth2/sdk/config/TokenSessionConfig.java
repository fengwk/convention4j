package fun.fengwk.convention4j.oauth2.sdk.config;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class TokenSessionConfig {

    /**
     * 最大的令牌存储时间，单位秒，默认30天，设置该时间时应同时考虑授权请求间隔时间和最大授权时间
     * （例如考虑用户使用oauth2接口的频次+最大授权时间，该值最大无需超过最大授权时间）
     */
    private int maxStoreSeconds = 60 * 60 * 24 * 30;

}
