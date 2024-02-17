package fun.fengwk.convention4j.oauth2.server.manager;

import fun.fengwk.convention4j.common.StringUtils;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.server.repo.TestClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Component
public class TestOAuth2ClientManager implements OAuth2ClientManager {

    private final TestClientRepository clientRepository;

    @Override
    public OAuth2Client getClient(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }
        return clientRepository.getByClientId(clientId);
    }

}
