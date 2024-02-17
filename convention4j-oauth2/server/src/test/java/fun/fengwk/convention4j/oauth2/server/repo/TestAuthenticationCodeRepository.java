package fun.fengwk.convention4j.oauth2.server.repo;

import fun.fengwk.convention4j.common.Pair;
import fun.fengwk.convention4j.oauth2.server.model.AuthenticationCode;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author fengwk
 */
@Repository
public class TestAuthenticationCodeRepository implements AuthenticationCodeRepository {

    private final ConcurrentMap<String, Pair<AuthenticationCode, Long>> map = new ConcurrentHashMap<>();

    @Override
    public boolean add(AuthenticationCode authenticationCode, int expireSeconds) {
        map.put(authenticationCode.getCode(), Pair.of(authenticationCode, System.currentTimeMillis() + expireSeconds * 1000L));
        return true;
    }

    @Override
    public boolean remove(String code) {
        return map.remove(code) != null;
    }

    @Override
    public AuthenticationCode get(String code) {
        Pair<AuthenticationCode, Long> pair = map.get(code);
        if (pair == null) {
            return null;
        }
        if (System.currentTimeMillis() > pair.getValue()) {
            map.remove(code);
            return null;
        }
        return pair.getKey();
    }

}
