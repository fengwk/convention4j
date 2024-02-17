package fun.fengwk.convention4j.oauth2.server.model.context;

/**
 * @author fengwk
 */
public interface ClientCredentialsTokenContext extends TokenContext {

    /**
     * 表示申请的权限范围
     */
    String getScope();

    /**
     * 必须，授权主体id
     */
    String getSubjectId();

}
