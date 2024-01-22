package fun.fengwk.convention4j.oauth2.core.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.page.Pages;
import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Repository
public class TestOAuth2ClientClientRepository
    extends AbstractTestRepository<StandardOAuth2Client, String>
    implements StandardOAuth2ClientRepository<StandardOAuth2Client> {

    @Override
    protected String getId(StandardOAuth2Client client) {
        return NullSafe.map(client, StandardOAuth2Client::getClientId);
    }

    @Override
    public boolean add(StandardOAuth2Client client) {
        return doInsert(client);
    }

    @Override
    public boolean updateByClientId(StandardOAuth2Client client) {
        return doUpdateById(client);
    }

    @Override
    public boolean updateClientId(String clientId, String newClientId) {
        return doUpdate(c -> Objects.equals(c.getClientId(), clientId), c -> {
            if (!Objects.equals(c.getClientId(), newClientId)) {
                c.setClientId(newClientId);
                return true;
            }
            return false;
        }) > 0;
    }

    @Override
    public boolean removeByClientId(String clientId) {
        return doDeleteById(clientId);
    }

    @Override
    public StandardOAuth2Client getByClientId(String clientId) {
        return doGetById(clientId);
    }

    @Override
    public boolean existsByClientId(String clientId) {
        return doGetById(clientId) != null;
    }

    @Override
    public Page<StandardOAuth2Client> page(PageQuery pageQuery, String clientIdPrefix, String namePrefix) {
        List<StandardOAuth2Client> clients = doList(
            c -> c.getClientId().startsWith(clientIdPrefix) || c.getName().startsWith(namePrefix));
        long totalCount = clients.size();
        long offset = Pages.queryOffset(pageQuery);
        int limit = Pages.queryLimit(pageQuery);
        clients = clients.stream().skip(offset).limit(limit).collect(Collectors.toList());
        return Pages.page(pageQuery, clients, totalCount);
    }

}
