package fun.fengwk.convention4j.oauth2.server.util;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class SsoIdInfo {

    /**
     * 单点登录id
     */
    private String id;

    /**
     * 过期的秒时间戳
     */
    private int et;

}
