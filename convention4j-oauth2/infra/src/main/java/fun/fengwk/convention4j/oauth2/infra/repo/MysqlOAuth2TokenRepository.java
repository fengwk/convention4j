package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.oauth2.infra.mapper.OAuth2TokenMapper;
import fun.fengwk.convention4j.oauth2.infra.model.OAuth2TokenDO;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.oauth2.server.repo.OAuth2TokenRepository;
import lombok.AllArgsConstructor;

/**
 * @author fengwk
 */
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
        return oauth2TokenMapper.insertSelective(oauth2TokenDO) > 0;
    }

    @Override
    public boolean updateById(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        OAuth2TokenDO oauth2TokenDO = convert(oauth2Token);
        return oauth2TokenMapper.updateById(oauth2TokenDO) > 0;
    }

    @Override
    public boolean removeByAccessToken(String accessToken) {
        return oauth2TokenMapper.deleteByAccessToken(accessToken) > 0;
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
    public OAuth2Token getBySsoId(String ssoId) {
        OAuth2TokenDO oauth2TokenDO = oauth2TokenMapper.getBySsoId(ssoId);
        return convert(oauth2TokenDO);
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
        oauth2TokenDO.setLastRefreshTime(oauth2Token.getLastRefreshTime());
        oauth2TokenDO.setAuthorizeTime(oauth2Token.getAuthorizeTime());
        return oauth2TokenDO;
    }

    private OAuth2Token convert(OAuth2TokenDO oauth2TokenDO) {
        if (oauth2TokenDO == null) {
            return null;
        }
        OAuth2Token oAuth2Token = new OAuth2Token();
        oAuth2Token.setId(oauth2TokenDO.getId());
        oAuth2Token.setClientId(oauth2TokenDO.getClientId());
        oAuth2Token.setSubjectId(oauth2TokenDO.getSubjectId());
        oAuth2Token.setScope(oauth2TokenDO.getScope());
        oAuth2Token.setTokenType(oauth2TokenDO.getTokenType());
        oAuth2Token.setAccessToken(oauth2TokenDO.getAccessToken());
        oAuth2Token.setRefreshToken(oauth2TokenDO.getRefreshToken());
        oAuth2Token.setSsoId(oauth2TokenDO.getSsoId());
        oAuth2Token.setLastRefreshTime(oauth2TokenDO.getLastRefreshTime());
        oAuth2Token.setAuthorizeTime(oauth2TokenDO.getAuthorizeTime());
        return oAuth2Token;
    }

}
