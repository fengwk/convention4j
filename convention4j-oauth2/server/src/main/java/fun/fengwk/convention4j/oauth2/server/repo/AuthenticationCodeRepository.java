package fun.fengwk.convention4j.oauth2.server.repo;

import fun.fengwk.convention4j.oauth2.server.model.AuthenticationCode;

/**
 * 授权码缓存
 *
 * @author fengwk
 */
public interface AuthenticationCodeRepository {

    /**
     * 添加授权码
     *
     * @param authenticationCode 授权码
     * @param expireSeconds      缓存时间，单位/秒
     */
    boolean add(AuthenticationCode authenticationCode, int expireSeconds);

    /**
     * 删除授权码
     *
     * @param code 授权码
     * @return 是否有授权码被删除
     */
    boolean remove(String code);

    /**
     * 获取授权码
     *
     * @param code 授权码
     * @return 授权码BO
     */
    AuthenticationCode get(String code);

}
