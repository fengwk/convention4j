package fun.fengwk.convention4j.oauth2.core;

import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;
import fun.fengwk.convention4j.oauth2.core.model.User;
import fun.fengwk.convention4j.oauth2.core.model.UserCertificate;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientCreateDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientUpdateDTO;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author fengwk
 */
@Configuration
public class TestOAuth2Configuration extends StandardOAuth2ConfigureTemplate<
    List<User>,
    UserCertificate,
    StandardOAuth2Client,
    StandardOAuth2ClientDTO,
    StandardOAuth2ClientCreateDTO,
    StandardOAuth2ClientUpdateDTO> {
}
