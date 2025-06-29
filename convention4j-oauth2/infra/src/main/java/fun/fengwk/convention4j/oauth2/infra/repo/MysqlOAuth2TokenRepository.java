package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.oauth2.infra.mapper.OAuth2TokenMapper;
import fun.fengwk.convention4j.oauth2.infra.model.OAuth2TokenDO;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class MysqlOAuth2TokenRepository implements OAuth2TokenRepository {

    private final NamespaceIdGenerator<Long> idGenerator;
    private final OAuth2TokenMapper oauth2TokenMapper;

    @Override
    public long generateId() {
        return idGenerator.next(OAuth2Token.class);
    }

    @Override
    public boolean add(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        OAuth2TokenDO oauth2TokenDO = convert(oauth2Token);
        boolean result = oauth2TokenMapper.insertSelective(oauth2TokenDO) > 0;
        log.debug("Add oauth2 token to mysql, oauth2Token: {}, result: {}", oauth2Token, result);
        return result;
    }

    @Override
    public boolean updateById(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        OAuth2TokenDO oauth2TokenDO = convert(oauth2Token);
        boolean result = oauth2TokenMapper.updateById(oauth2TokenDO) > 0;
        log.debug("Update oauth2 token to mysql, oauth2Token: {}, result: {}", oauth2Token, result);
        return result;
    }

    @Override
    public boolean removeById(long id) {
        boolean result = oauth2TokenMapper.deleteById(id) > 0;
        log.debug("Remove oauth2 token from mysql, accessToken: {}, result: {}", id, result);
        return result;
    }

    @Override
    public OAuth2Token getByAccessToken(String accessToken) {
        OAuth2TokenDO oauth2TokenDO = oauth2TokenMapper.getByAccessToken(accessToken);
        return convert(oauth2TokenDO);
    }

    @Override
    public OAuth2Token getByRefreshToken(String refreshToken) {
        OAuth2TokenDO oauth2TokenDO = oauth2TokenMapper.getByRefreshToken(refreshToken);
        return convert(oauth2TokenDO);
    }

    @Override
    public OAuth2Token getBySsoIdAndSsoDomain(String ssoId, String ssoDomain) {
        OAuth2TokenDO oauth2TokenDO = oauth2TokenMapper.getBySsoIdAndSsoDomain(ssoId, ssoDomain);
        return convert(oauth2TokenDO);
    }

    @Override
    public List<OAuth2Token> listBySsoId(String ssoId) {
        return oauth2TokenMapper.listBySsoId(ssoId).stream()
            .map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<OAuth2Token> listBySubjectId(String subjectId) {
        List<OAuth2TokenDO> oauth2TokenDOs = oauth2TokenMapper.listBySubjectId(subjectId);
        return oauth2TokenDOs.stream().map(this::convert).collect(Collectors.toList());
    }

    private OAuth2TokenDO convert(OAuth2Token oauth2Token) {
        if (oauth2Token == null) {
            return null;
        }
        OAuth2TokenDO oauth2TokenDO = new OAuth2TokenDO();
        oauth2TokenDO.setId(oauth2Token.getId());
        oauth2TokenDO.setClientId(oauth2Token.getClientId());
        oauth2TokenDO.setSubjectId(oauth2Token.getSubjectId());
        oauth2TokenDO.setScope(oauth2Token.getScope());
        oauth2TokenDO.setTokenType(oauth2Token.getTokenType());
        oauth2TokenDO.setAccessToken(oauth2Token.getAccessToken());
        oauth2TokenDO.setRefreshToken(oauth2Token.getRefreshToken());
        oauth2TokenDO.setSsoId(oauth2Token.getSsoId());
        oauth2TokenDO.setSsoDomain(oauth2Token.getSsoDomain());
        oauth2TokenDO.setLastRefreshTime(oauth2Token.getLastRefreshTime());
        oauth2TokenDO.setAuthorizeTime(oauth2Token.getAuthorizeTime());
        return oauth2TokenDO;
    }

    private OAuth2Token convert(OAuth2TokenDO oauth2TokenDO) {
        if (oauth2TokenDO == null) {
            return null;
        }
        OAuth2Token oauth2Token = new OAuth2Token();
        oauth2Token.setId(oauth2TokenDO.getId());
        oauth2Token.setClientId(oauth2TokenDO.getClientId());
        oauth2Token.setSubjectId(oauth2TokenDO.getSubjectId());
        oauth2Token.setScope(oauth2TokenDO.getScope());
        oauth2Token.setTokenType(oauth2TokenDO.getTokenType());
        oauth2Token.setAccessToken(oauth2TokenDO.getAccessToken());
        oauth2Token.setRefreshToken(oauth2TokenDO.getRefreshToken());
        oauth2Token.setSsoId(oauth2TokenDO.getSsoId());
        oauth2Token.setSsoDomain(oauth2TokenDO.getSsoDomain());
        oauth2Token.setLastRefreshTime(oauth2TokenDO.getLastRefreshTime());
        oauth2Token.setAuthorizeTime(oauth2TokenDO.getAuthorizeTime());
        return oauth2Token;
    }

}
