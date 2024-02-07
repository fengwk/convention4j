package fun.fengwk.convention4j.oauth2.core.service;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;
import fun.fengwk.convention4j.oauth2.core.repo.StandardOAuth2ClientRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ClientStatus;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientCreateDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public abstract class StandardOAuth2ClientBackendServiceImpl<
    CLIENT extends StandardOAuth2Client,
    CLIENT_DTO extends StandardOAuth2ClientDTO,
    CLIENT_CREATE_DTO extends StandardOAuth2ClientCreateDTO,
    CLIENT_UPDATE_DTO extends StandardOAuth2ClientUpdateDTO>
    implements StandardOAuth2ClientBackendService<CLIENT_DTO, CLIENT_CREATE_DTO, CLIENT_UPDATE_DTO> {

    private final StandardOAuth2ClientRepository<CLIENT> oauth2ClientClientRepository;

    protected abstract CLIENT newClient();

    protected abstract CLIENT_DTO newClientDTO();

    @Override
    public CLIENT_DTO createClient(CLIENT_CREATE_DTO createDTO) {
        if (oauth2ClientClientRepository.existsByClientId(createDTO.getClientId())) {
            log.warn("client already exists, clientId: {}", createDTO.getClientId());
            throw OAuth2ErrorCodes.CLIENT_ALREADY_EXISTS.asThrowable();
        }

        CLIENT client = create(createDTO);
        oauth2ClientClientRepository.add(client);
        return toDTO(client, this::newClientDTO);
    }

    @Override
    public CLIENT_DTO updateClient(CLIENT_UPDATE_DTO updateDTO) {
        CLIENT client = oauth2ClientClientRepository.getByClientId(updateDTO.getClientId());
        if (client == null) {
            log.warn("client not found, clientId: {}", updateDTO.getClientId());
            throw OAuth2ErrorCodes.CLIENT_NOT_FOUND.asThrowable();
        }

        if (update(client, updateDTO)) {
            oauth2ClientClientRepository.updateByClientId(client);
        }
        return toDTO(client, this::newClientDTO);
    }

    @Override
    public void updateClientId(String clientId, String newClientId) {
        if (oauth2ClientClientRepository.existsByClientId(clientId)) {
            oauth2ClientClientRepository.updateClientId(clientId, newClientId);
        }
    }

    @Override
    public void removeClient(String clientId) {
        if (oauth2ClientClientRepository.existsByClientId(clientId)) {
            oauth2ClientClientRepository.removeByClientId(clientId);
        }
    }

    @Override
    public void enableClient(String clientId) {
        CLIENT client = oauth2ClientClientRepository.getByClientId(clientId);
        if (client != null && !Objects.equals(client.getStatus(), OAuth2ClientStatus.ENABLE)) {
            client.setStatus(OAuth2ClientStatus.ENABLE);
            oauth2ClientClientRepository.updateByClientId(client);
        }
    }

    @Override
    public void disableClient(String clientId) {
        CLIENT client = oauth2ClientClientRepository.getByClientId(clientId);
        if (client != null && !Objects.equals(client.getStatus(), OAuth2ClientStatus.DISABLE)) {
            client.setStatus(OAuth2ClientStatus.DISABLE);
            oauth2ClientClientRepository.updateByClientId(client);
        }
    }

    @Override
    public Page<CLIENT_DTO> pageClient(PageQuery pageQuery, String keyword) {
        Page<CLIENT> page = oauth2ClientClientRepository.page(pageQuery, keyword, keyword);
        return page.map(c -> toDTO(c, this::newClientDTO));
    }

    public CLIENT create(CLIENT_CREATE_DTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        checkRedirectUris(createDTO.getRedirectUris());
        CLIENT client = newClient();
        client.setClientId(createDTO.getClientId());
        client.setName(createDTO.getName());
        client.setDescription(createDTO.getDescription());
        client.setSecret(generateSecret());
        client.setStatus(createDTO.getStatus());
        client.setModes(createDTO.getModes());
        client.setRedirectUris(createDTO.getRedirectUris());
        client.setScopeUnits(createDTO.getScopeUnits());
        client.setAuthorizationCodeExpireSeconds(createDTO.getAuthorizationCodeExpireSeconds());
        client.setAccessTokenExpireSeconds(createDTO.getAccessTokenExpireSeconds());
        client.setRefreshTokenExpireSeconds(createDTO.getRefreshTokenExpireSeconds());
        client.setAuthorizeExpireSeconds(createDTO.getAuthorizeExpireSeconds());
        client.setAllowRefreshToken(createDTO.isAllowRefreshToken());
        client.setAllowSso(createDTO.isAllowSso());
        LocalDateTime now = LocalDateTime.now();
        client.setCreateTime(now);
        client.setUpdateTime(now);
        return client;
    }

    private static String generateSecret() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return sb.toString();
    }

    public boolean update(CLIENT client, CLIENT_UPDATE_DTO updateDTO) {
        boolean updated = false;
        if (!Objects.equals(updateDTO.getName(), client.getName())) {
            client.setName(updateDTO.getName());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getDescription(), client.getDescription())) {
            client.setDescription(updateDTO.getDescription());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getStatus(), client.getStatus())) {
            client.setStatus(updateDTO.getStatus());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getModes(), client.getModes())) {
            client.setModes(updateDTO.getModes());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getRedirectUris(), client.getRedirectUris())) {
            checkRedirectUris(updateDTO.getRedirectUris());
            client.setRedirectUris(updateDTO.getRedirectUris());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getScopeUnits(), client.getScopeUnits())) {
            client.setScopeUnits(updateDTO.getScopeUnits());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getAuthorizationCodeExpireSeconds(), client.getAuthorizationCodeExpireSeconds())) {
            client.setAuthorizationCodeExpireSeconds(updateDTO.getAuthorizationCodeExpireSeconds());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getAccessTokenExpireSeconds(), client.getAccessTokenExpireSeconds())) {
            client.setAccessTokenExpireSeconds(updateDTO.getAccessTokenExpireSeconds());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getRefreshTokenExpireSeconds(), client.getRefreshTokenExpireSeconds())) {
            client.setRefreshTokenExpireSeconds(updateDTO.getRefreshTokenExpireSeconds());
            updated = true;
        }
        if (!Objects.equals(updateDTO.getAuthorizeExpireSeconds(), client.getAuthorizeExpireSeconds())) {
            client.setAuthorizeExpireSeconds(updateDTO.getAuthorizeExpireSeconds());
            updated = true;
        }
        if (!Objects.equals(updateDTO.isAllowRefreshToken(), client.isAllowRefreshToken())) {
            client.setAllowRefreshToken(updateDTO.isAllowRefreshToken());
            updated = true;
        }
        if (!Objects.equals(updateDTO.isAllowSso(), client.isAllowSso())) {
            client.setAllowSso(updateDTO.isAllowSso());
            updated = true;
        }
        if (updated) {
            client.setUpdateTime(LocalDateTime.now());
        }
        return updated;
    }

    public CLIENT_DTO toDTO(CLIENT client, Supplier<CLIENT_DTO> dtoSupplier) {
        if (client == null) {
            return null;
        }
        CLIENT_DTO dto = dtoSupplier.get();
        dto.setClientId(client.getClientId());
        dto.setName(client.getName());
        dto.setDescription(client.getDescription());
        dto.setSecret(client.getSecret());
        dto.setStatus(client.getStatus());
        dto.setModes(client.getModes());
        dto.setRedirectUris(client.getRedirectUris());
        dto.setScopeUnits(client.getScopeUnits());
        dto.setAuthorizationCodeExpireSeconds(client.getAuthorizationCodeExpireSeconds());
        dto.setAccessTokenExpireSeconds(client.getAccessTokenExpireSeconds());
        dto.setRefreshTokenExpireSeconds(client.getRefreshTokenExpireSeconds());
        dto.setAuthorizeExpireSeconds(client.getAuthorizeExpireSeconds());
        dto.setAllowRefreshToken(client.isAllowRefreshToken());
        dto.setAllowSso(client.isAllowSso());
        dto.setUpdateTime(client.getUpdateTime());
        dto.setCreateTime(client.getCreateTime());
        return dto;
    }

    private void checkRedirectUris(Set<String> redirectUris) {
        if (redirectUris != null) {
            for (String redirectUri : redirectUris) {
                try {
                    UriComponentsBuilder.fromUriString(redirectUri);
                } catch (IllegalArgumentException ex) {
                    log.warn("invalid redirectUri, redirectUri: {}", redirectUri);
                    throw OAuth2ErrorCodes.INVALID_REDIRECT_URI.asThrowable();
                }
            }
        }
    }

}
