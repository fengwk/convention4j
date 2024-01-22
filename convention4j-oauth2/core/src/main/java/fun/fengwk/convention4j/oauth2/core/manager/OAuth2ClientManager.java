package fun.fengwk.convention4j.oauth2.core.manager;

import fun.fengwk.convention4j.oauth2.core.model.OAuth2Client;
import fun.fengwk.convention4j.oauth2.share.constant.OAuth2ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth2门面，提供OAuth2相关服务的基础能力
 *
 * @author fengwk
 */
public interface OAuth2ClientManager {

    Logger log = LoggerFactory.getLogger(OAuth2ClientManager.class);

    /**
     * 获取指定id的客户端
     *
     * @param clientId 客户端id
     * @return 客户端
     */
    OAuth2Client getClient(String clientId);

    /**
     * 获取指定id的客户端，如果不存在将抛出异常
     *
     * @param clientId 客户端id
     * @return 客户端
     */
    default OAuth2Client getClientRequired(String clientId) {
        OAuth2Client client = getClient(clientId);
        if (client == null) {
            log.warn("client not found, clientId: {}", clientId);
            throw OAuth2ErrorCodes.CLIENT_NOT_FOUND.asThrowable();
        }
        return client;
    }

}
