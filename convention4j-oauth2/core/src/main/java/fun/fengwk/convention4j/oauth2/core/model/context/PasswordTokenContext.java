package fun.fengwk.convention4j.oauth2.core.model.context;

/**
 * @author fengwk
 */
public interface PasswordTokenContext<CERTIFICATE> extends TokenContext {

    /**
     * 表示申请的权限范围
     */
    String getScope();

    /**
     * 认证信息
     */
    CERTIFICATE getCertificate();

}
