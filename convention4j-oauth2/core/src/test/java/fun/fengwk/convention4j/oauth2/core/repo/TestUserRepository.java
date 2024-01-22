package fun.fengwk.convention4j.oauth2.core.repo;

import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.oauth2.core.model.User;
import fun.fengwk.convention4j.springboot.test.starter.repo.AbstractTestRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * @author fengwk
 */
@Repository
public class TestUserRepository extends AbstractTestRepository<User, Long> {

    @Override
    protected Long getId(User user) {
        return NullSafe.map(user, User::getId);
    }

    public void add(User user) {
        doInsert(user);
    }

    public User getById(Long id) {
        return doGetById(id);
    }

    public User getByUsername(String username) {
        return doGet(user -> Objects.equals(user.getUsername(), username));
    }

}
