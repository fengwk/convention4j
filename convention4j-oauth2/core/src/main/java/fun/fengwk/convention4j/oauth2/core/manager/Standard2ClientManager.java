package fun.fengwk.convention4j.oauth2.core.manager;

import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.repo.OAuth2ClientRepository;
import lombok.AllArgsConstructor;

/**
 * @author fengwk
 */
@AllArgsConstructor
public class Standard2ClientManager<CLIENT extends OAuth2Client> implements OAuth2ClientManager {

    private final OAuth2ClientRepository<CLIENT> oauth2ClientRepository;

    @Override
    public OAuth2Client getClient(String clientId) {
        return oauth2ClientRepository.getByClientId(clientId);
    }

}
