package fun.fengwk.convention4j.oauth2.sdk.manager;

/**
 * @author fengwk
 */
public interface OAuth2StateSessionManager {

    String generateState();

    boolean invalidState(String state);

    boolean verifyState(String state);

}
