package fun.fengwk.convention4j.oauth2.server.repo;

import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;

import java.util.List;

/**
 * OAuth2令牌仓储
 *
 * @author fengwk
 */
public interface OAuth2TokenRepository {

    /**
     * 生成令牌id
     *
     * @return 令牌id
     */
    long generateId();

    /**
     * 添加OAuth2令牌
     *
     * @param oauth2Token            OAuth2令牌
     * @param authorizeExpireSeconds 授权过期时间，单位秒
     * @return 是否添加成功
     */
    boolean add(OAuth2Token oauth2Token, int authorizeExpireSeconds);

    /**
     * 根据id更新OAuth2令牌
     *
     * @param oauth2Token OAuth2令牌
     * @param authorizeExpireSeconds 授权过期时间，单位秒
     * @return 是否更新成功
     */
    boolean updateById(OAuth2Token oauth2Token, int authorizeExpireSeconds);

    /**
     * 通过访问令牌删除
     *
     * @param accessToken 访问令牌
     * @return 是否发生了删除
     */
    boolean removeByAccessToken(String accessToken);

    /**
     * 通过访问令牌获取OAuth2令牌
     *
     * @param accessToken 访问令牌
     * @return OAuth2令牌
     */
    OAuth2Token getByAccessToken(String accessToken);

    /**
     * 通过刷新令牌获取OAuth2令牌
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2令牌
     */
    OAuth2Token getByRefreshToken(String refreshToken);

    /**
     * 使用单点登陆id获取
     *
     * @param ssoId 单点登陆id
     * @return OAuth2令牌
     */
    OAuth2Token getBySsoId(String ssoId);

    /**
     * 通过subjectId获取所有令牌
     *
     * @param subjectId 主体id
     * @return 主体的所有令牌
     */
    List<OAuth2Token> listBySubjectId(String subjectId);

}
