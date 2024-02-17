package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface RefreshTokenContext extends TokenContext {

    /**
     * 必须，刷新令牌
     */
    String getRefreshToken();

}
