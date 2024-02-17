package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface AuthenticationCodeTokenContext extends TokenContext {

    /**
     * 必须，表示上一步获得的授权码
     */
    String getCode();

    /**
     * 必须，表示重定向URI，必须与authorize步骤中的参数值保持一致
     */
    String getRedirectUri();

}