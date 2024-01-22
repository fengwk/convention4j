package fun.fengwk.convention4j.oauth2.infra.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.common.page.Pages;
import fun.fengwk.convention4j.oauth2.core.OAuth2Properties;
import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;
import fun.fengwk.convention4j.oauth2.core.repo.StandardOAuth2ClientRepository;
import fun.fengwk.convention4j.oauth2.infra.mapper.StandardOAuth2ClientMapper;
import fun.fengwk.convention4j.oauth2.infra.model.StandardOAuth2ClientDO;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author fengwk
 */
@AllArgsConstructor
public abstract class MysqlStandardOAuth2ClientRepository<CLIENT extends StandardOAuth2Client, CLIENT_DO extends StandardOAuth2ClientDO>
    implements StandardOAuth2ClientRepository<CLIENT> {

    private final OAuth2Properties oauth2Properties;
    private final StandardOAuth2ClientMapper<CLIENT_DO> clientMapper;

    protected abstract CLIENT newClient();

    protected abstract CLIENT_DO newClientDO();

    @PostConstruct
    public void init() {
        if (oauth2Properties.isAutoInitInfra()) {
            clientMapper.createTableIfNotExists();
        }
    }

    @Override
    public boolean add(CLIENT client) {
        if (client == null) {
            return false;
        }
        CLIENT_DO clientDO = convert(client);
        return clientMapper.insertSelective(clientDO) > 0;
    }

    @Override
    public boolean updateByClientId(CLIENT client) {
        if (client == null) {
            return false;
        }
        CLIENT_DO clientDO = convert(client);
        return clientMapper.updateByClientIdSelective(clientDO) > 0;
    }

    @Override
    public boolean updateClientId(String clientId, String newClientId) {
        return clientMapper.updateClientId(clientId, newClientId) > 0;
    }

    @Override
    public boolean removeByClientId(String clientId) {
        return clientMapper.deleteByClientId(clientId) > 0;
    }

    @Override
    public CLIENT getByClientId(String clientId) {
        CLIENT_DO CLIENT_DO = clientMapper.getByClientId(clientId);
        return convert(CLIENT_DO);
    }

    @Override
    public boolean existsByClientId(String clientId) {
        return clientMapper.countByClientId(clientId) > 0;
    }

    @Override
    public Page<CLIENT> page(PageQuery pageQuery, String clientIdPrefix, String namePrefix) {
        long offset = Pages.queryOffset(pageQuery);
        int limit = Pages.queryLimit(pageQuery);
        if (StringUtils.isEmpty(clientIdPrefix)) {
            clientIdPrefix = null;
        }
        if (StringUtils.isEmpty(namePrefix)) {
            namePrefix = null;
        }
        long totalCount = clientMapper.countByClientIdStartingWithOrNameStartingWith(clientIdPrefix, namePrefix);
        List<CLIENT_DO> clientDOs = clientMapper.pageByClientIdStartingWithOrNameStartingWithOrderByCreateTimeDesc(
            offset, limit, clientIdPrefix, namePrefix);
        return Pages.page(pageQuery, clientDOs, totalCount).map(this::convert);
    }

    public CLIENT_DO convert(CLIENT client) {
        if (client == null) {
            return null;
        }
        CLIENT_DO clientDO = newClientDO();
        clientDO.setClientId(client.getClientId());
        clientDO.setName(client.getName());
        clientDO.setDescription(client.getDescription());
        clientDO.setSecret(client.getSecret());
        clientDO.setStatus(client.getStatus());
        clientDO.setModes(client.getModes());
        clientDO.setRedirectUris(client.getRedirectUris());
        clientDO.setScopeUnits(client.getScopeUnits());
        clientDO.setAuthorizationCodeExpireSeconds(client.getAuthorizationCodeExpireSeconds());
        clientDO.setAccessTokenExpireSeconds(client.getAccessTokenExpireSeconds());
        clientDO.setRefreshTokenExpireSeconds(client.getRefreshTokenExpireSeconds());
        clientDO.setAuthorizationExpireSeconds(client.getAuthorizeExpireSeconds());
        clientDO.setAllowRefreshToken(client.isAllowRefreshToken());
        clientDO.setAllowSso(client.isAllowSso());
        clientDO.setCreateTime(client.getCreateTime());
        clientDO.setUpdateTime(client.getUpdateTime());
        return clientDO;
    }

    public CLIENT convert(CLIENT_DO clientDO) {
        if (clientDO == null) {
            return null;
        }
        CLIENT client = newClient();
        client.setClientId(clientDO.getClientId());
        client.setName(clientDO.getName());
        client.setDescription(clientDO.getDescription());
        client.setSecret(clientDO.getSecret());
        client.setStatus(clientDO.getStatus());
        client.setModes(clientDO.getModes());
        client.setRedirectUris(clientDO.getRedirectUris());
        client.setScopeUnits(clientDO.getScopeUnits());
        client.setAuthorizationCodeExpireSeconds(clientDO.getAuthorizationCodeExpireSeconds());
        client.setAccessTokenExpireSeconds(clientDO.getAccessTokenExpireSeconds());
        client.setRefreshTokenExpireSeconds(clientDO.getRefreshTokenExpireSeconds());
        client.setAuthorizeExpireSeconds(clientDO.getAuthorizationExpireSeconds());
        client.setAllowRefreshToken(clientDO.isAllowRefreshToken());
        client.setAllowSso(clientDO.isAllowSso());
        client.setCreateTime(clientDO.getCreateTime());
        client.setUpdateTime(clientDO.getUpdateTime());
        return client;
    }

}
