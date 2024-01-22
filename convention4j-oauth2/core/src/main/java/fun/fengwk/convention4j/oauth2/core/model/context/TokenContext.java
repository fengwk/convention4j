package fun.fengwk.convention4j.oauth2.core.model.context;

/**
 * @author fengwk
 */
public interface TokenContext {

    /**
     * 必须，表示使用的授权模式
     */
    String getGrantType();

    /**
     * 必须，客户端id
     */
    String getClientId();

    /**
     * 必须，客户端密钥
     */
    String getClientSecret();

}
