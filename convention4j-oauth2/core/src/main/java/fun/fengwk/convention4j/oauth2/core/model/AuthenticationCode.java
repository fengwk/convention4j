package fun.fengwk.convention4j.oauth2.core.model;

import fun.fengwk.convention4j.oauth2.share.constant.ResponseType;
import lombok.Data;

import java.util.UUID;

/**
 * 授权码
 * @author fengwk
 */
@Data
public class AuthenticationCode {

    /**
     * 授权码
     */
    private String code;

    /**
     * 主体id
     */
    private String subjectId;

    /**
     * 响应类型
     */
    private ResponseType responseType;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 重定向地址
     */
    private String redirectUri;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 单点登陆id
     */
    private String ssoId;

    /**
     * 是否为sso认证
     */
    private boolean ssoAuthenticate;

    /**
     * 生成授权码BO
     */
    public static AuthenticationCode generate(
        String subjectId,
        ResponseType responseType,
        String clientId,
        String redirectUri,
        String scope,
        String ssoId,
        boolean ssoAuthenticate) {
        AuthenticationCode authenticationCode = new AuthenticationCode();
        authenticationCode.setCode(generateCode());
        authenticationCode.setSubjectId(subjectId);
        authenticationCode.setResponseType(responseType);
        authenticationCode.setClientId(clientId);
        authenticationCode.setRedirectUri(redirectUri);
        authenticationCode.setScope(scope);
        authenticationCode.setSsoId(ssoId);
        authenticationCode.setSsoAuthenticate(ssoAuthenticate);
        return authenticationCode;
    }

    /**
     * 生成授权码
     * @return 授权码
     */
    private static String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return sb.toString();
    }

}
