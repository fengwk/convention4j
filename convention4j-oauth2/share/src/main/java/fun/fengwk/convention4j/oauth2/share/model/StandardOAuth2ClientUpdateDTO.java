package fun.fengwk.convention4j.oauth2.share.model;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class StandardOAuth2ClientUpdateDTO extends StandardOAuth2ClientEditablePropertiesDTO {

    /**
     * 客户端标识符，全局唯一
     */
    private String clientId;

}
