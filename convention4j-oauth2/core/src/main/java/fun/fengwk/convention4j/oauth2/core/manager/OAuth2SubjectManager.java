package fun.fengwk.convention4j.oauth2.core.manager;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;

import java.util.Set;

/**
 * @author fengwk
 */
public interface OAuth2SubjectManager<SUBJECT, CERTIFICATE> {

    /**
     * 通过凭证信息进行认证
     *
     * @param client      客户端
     * @param certificate 凭证信息
     * @return 认证成功返回主体id，否则返回null
     */
    Result<String> authenticate(OAuth2Client client, CERTIFICATE certificate);

    /**
     * 通过主体id和指定作用域获取主体
     *
     * @param client     客户端
     * @param subjectId  主体id
     * @param scopeUnits 作用域单元集合
     * @return 主体
     */
    Result<SUBJECT> getSubject(OAuth2Client client, String subjectId, Set<String> scopeUnits);

}
