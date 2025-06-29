package fun.fengwk.convention4j.oauth2.infra.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.oauth2.infra.model.OAuth2TokenDO;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.CacheableMapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper(tableName = "oauth2_token")
public interface OAuth2TokenMapper extends CacheableMapper<OAuth2TokenDO, Long> {

    int insertSelective(OAuth2TokenDO oauth2TokenDO);

    int updateById(OAuth2TokenDO oauth2TokenDO);

    int deleteById(long id);

    OAuth2TokenDO getByAccessToken(String accessToken);

    OAuth2TokenDO getByRefreshToken(String refreshToken);

    OAuth2TokenDO getBySsoIdAndSsoDomain(@Param("ssoId") String ssoId, @Param("ssoDomain") String ssoDomain);

    List<OAuth2TokenDO> listBySsoId(String ssoId);

    List<OAuth2TokenDO> listBySubjectId(String subjectId);

}
