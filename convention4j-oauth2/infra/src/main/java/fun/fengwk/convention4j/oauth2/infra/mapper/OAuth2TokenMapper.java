package fun.fengwk.convention4j.oauth2.infra.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.oauth2.infra.model.OAuth2TokenDO;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.CacheableMapper;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper(tableName = "oauth2_token")
public interface OAuth2TokenMapper extends CacheableMapper<OAuth2TokenDO, Long> {

    int insertSelective(OAuth2TokenDO oauth2TokenDO);

    int updateById(OAuth2TokenDO oauth2TokenDO);

    int deleteByAccessToken(String accessToken);

    OAuth2TokenDO getByAccessToken(String accessToken);

    OAuth2TokenDO getByRefreshToken(String refreshToken);

    OAuth2TokenDO getBySsoId(String ssoId);

    List<OAuth2TokenDO> listBySubjectId(String subjectId);

}
