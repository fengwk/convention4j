package fun.fengwk.convention4j.oauth2.server.config;

import fun.fengwk.convention4j.oauth2.server.constant.TestConstants;
import fun.fengwk.convention4j.oauth2.server.model.Client;
import fun.fengwk.convention4j.oauth2.server.model.User;
import fun.fengwk.convention4j.oauth2.server.repo.TestClientRepository;
import fun.fengwk.convention4j.oauth2.server.repo.TestUserRepository;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2Mode;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Component
public class TestDataInitializer {

    private final TestClientRepository clientRepository;
    private final TestUserRepository userRepository;

    @PostConstruct
    public void init() {
        initClients();
        initUsers();
    }

    public void initClients() {
        Client c1 = new Client();
        c1.setClientId(TestConstants.CLIENT1_ID);
        c1.setSecret(TestConstants.CLIENT1_SECRET);
        c1.setModes(new HashSet<>(Collections.singletonList(OAuth2Mode.AUTHORIZATION_CODE)));
        c1.setRedirectUris(new HashSet<>(Collections.singletonList(TestConstants.CLIENT1_REDIRECT_URI)));
        c1.setScopeUnits(TestConstants.CLIENT1_SCOPE_UNITS);
        c1.setAuthorizationCodeExpireSeconds(5);
        c1.setAccessTokenExpireSeconds(5);
        c1.setRefreshTokenExpireSeconds(10);
        c1.setAuthorizeExpireSeconds(20);
        c1.setAllowRefreshToken(true);
        c1.setAllowSso(true);
        clientRepository.add(c1);

        Client c2 = new Client();
        c2.setClientId(TestConstants.CLIENT2_ID);
        c2.setSecret("c2 secret");
        c2.setModes(new HashSet<>(Arrays.asList(OAuth2Mode.IMPLICIT, OAuth2Mode.PASSWORD, OAuth2Mode.CLIENT_CREDENTIALS)));
        c2.setRedirectUris(new HashSet<>(Collections.singletonList(TestConstants.CLIENT2_REDIRECT_URI)));
        c2.setScopeUnits(TestConstants.CLIENT2_SCOPE_UNITS);
        c2.setAuthorizationCodeExpireSeconds(5);
        c2.setAccessTokenExpireSeconds(5);
        c2.setRefreshTokenExpireSeconds(10);
        c2.setAuthorizeExpireSeconds(20);
        c2.setAllowRefreshToken(true);
        c2.setAllowSso(false);
        clientRepository.add(c2);
    }

    public void initUsers() {
        User u1 = new User();
        u1.setId(TestConstants.USER1_ID);
        u1.setUsername(TestConstants.USER1_USERNAME);
        u1.setPassword(TestConstants.USER1_PASSWORD);
        userRepository.add(u1);

        User u2 = new User();
        u2.setId(TestConstants.USER2_ID);
        u2.setUsername(TestConstants.USER2_USERNAME);
        u2.setPassword(TestConstants.USER2_PASSWORD);
        userRepository.add(u2);
    }

}
