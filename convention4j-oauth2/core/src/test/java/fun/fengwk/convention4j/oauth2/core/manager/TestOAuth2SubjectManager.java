package fun.fengwk.convention4j.oauth2.core.manager;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.User;
import fun.fengwk.convention4j.oauth2.core.model.UserCertificate;
import fun.fengwk.convention4j.oauth2.core.repo.TestUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Component
public class TestOAuth2SubjectManager implements OAuth2SubjectManager<User, UserCertificate> {

    private final TestUserRepository userRepository;

    @Override
    public Result<String> authenticate(OAuth2Client client, UserCertificate userCertificate) {
        if (userCertificate == null) {
            return Results.ok(null);
        }
        User user = userRepository.getByUsername(userCertificate.getUsername());
        String subjectId = user != null && Objects.equals(user.getPassword(), userCertificate.getPassword()) ?
            String.valueOf(user.getId()) : null;
        return Results.ok(subjectId);
    }

    @Override
    public Result<User> getSubject(OAuth2Client client, String subjectId, Set<String> scopeUnits) {
        User user = userRepository.getById(Long.valueOf(subjectId));
        return Results.ok(user);
    }

}
