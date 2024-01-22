package fun.fengwk.convention4j.oauth2.core.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;

/**
 * @author fengwk
 */
public interface StandardOAuth2ClientRepository<CLIENT extends StandardOAuth2Client> extends OAuth2ClientRepository<CLIENT> {
    
    boolean add(CLIENT client);

    boolean updateByClientId(CLIENT client);

    boolean updateClientId(String clientId, String newClientId);

    boolean removeByClientId(String clientId);

    CLIENT getByClientId(String clientId);

    boolean existsByClientId(String clientId);

    Page<CLIENT> page(PageQuery pageQuery, String clientIdPrefix, String namePrefix);

}
