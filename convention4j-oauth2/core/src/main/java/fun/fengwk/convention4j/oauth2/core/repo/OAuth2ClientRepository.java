package fun.fengwk.convention4j.oauth2.core.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;

/**
 * @author fengwk
 */
public interface OAuth2ClientRepository<CLIENT extends OAuth2Client> {
    
    boolean add(CLIENT client);

    boolean updateByClientId(CLIENT client);

    boolean updateClientId(String clientId, String newClientId);

    boolean removeByClientId(String clientId);

    CLIENT getByClientId(String clientId);

    boolean existsByClientId(String clientId);

    Page<CLIENT> page(PageQuery pageQuery, String clientIdPrefix, String namePrefix);

}
