package fun.fengwk.convention4j.oauth2.server.repo;

import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.model.Client;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * @author fengwk
 */
@Repository
public class TestClientRepository extends AbstractTestRepository<Client, String> {

    @Override
    protected String getId(Client client) {
        return NullSafe.map(client, Client::getClientId);
    }

    public boolean add(Client client) {
        return doInsert(client);
    }

    public boolean updateByClientId(Client client) {
        return doUpdateById(client);
    }

    public boolean updateClientId(String clientId, String newClientId) {
        return doUpdate(c -> Objects.equals(c.getClientId(), clientId), c -> {
            if (!Objects.equals(c.getClientId(), newClientId)) {
                c.setClientId(newClientId);
                return true;
            }
            return false;
        }) > 0;
    }

    public boolean removeByClientId(String clientId) {
        return doDeleteById(clientId);
    }

    public Client getByClientId(String clientId) {
        return doGetById(clientId);
    }

    public boolean existsByClientId(String clientId) {
        return doGetById(clientId) != null;
    }

}
