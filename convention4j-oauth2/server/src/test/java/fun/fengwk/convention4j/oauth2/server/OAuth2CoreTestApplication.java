package fun.fengwk.convention4j.oauth2.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@SpringBootApplication
public class OAuth2CoreTestApplication {

    // 测试命令
    // curl -X POST "http://localhost:8080/token?grantType=authorization_code&code=$(curl 'http://localhost:8080/authorize?responseType=code&clientId=c1&redirectUri=http%3A%2F%2Ffengwk.fun&scope=userInfo&state=123' -d '{"username":"u1","password":"111"}' -H 'Content-Type: applicatoin/json' -X GET -s | sed -nE 's/.+code=([^&]+)&.+/\1/p')&redirectUri=http%3A%2F%2Ffengwk.fun&clientId=c1&clientSecret=c1%20secret"
    public static void main(String[] args) {
        SpringApplication.run(OAuth2CoreTestApplication.class, args);
    }

}
