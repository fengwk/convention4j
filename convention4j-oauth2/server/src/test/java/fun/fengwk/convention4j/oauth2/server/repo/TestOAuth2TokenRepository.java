package fun.fengwk.convention4j.oauth2.server.repo;

import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.oauth2.server.model.OAuth2Token;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fengwk
 */
@Repository
public class TestOAuth2TokenRepository
    extends AbstractTestRepository<OAuth2Token, Long> implements OAuth2TokenRepository {

    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    protected Long getId(OAuth2Token oauth2Token) {
        return NullSafe.map(oauth2Token, OAuth2Token::getId);
    }

    @Override
    public long generateId() {
        return idGenerator.incrementAndGet();
    }

    @Override
    public boolean add(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        return doInsert(oauth2Token);
    }

    @Override
    public boolean updateById(OAuth2Token oauth2Token, int authorizeExpireSeconds) {
        return doUpdateById(oauth2Token);
    }

    @Override
    public boolean removeByAccessToken(String accessToken) {
        return doDelete(tk -> Objects.equals(tk.getAccessToken(), accessToken)) > 0;
    }

    @Override
    public OAuth2Token getByAccessToken(String accessToken) {
        return doGet(tk -> Objects.equals(tk.getAccessToken(), accessToken));
    }

    @Override
    public OAuth2Token getByRefreshToken(String refreshToken) {
        return doGet(tk -> Objects.equals(tk.getRefreshToken(), refreshToken));
    }

    @Override
    public OAuth2Token getBySsoId(String ssoId) {
        return doGet(tk -> Objects.equals(tk.getSsoId(), ssoId));
    }

}
