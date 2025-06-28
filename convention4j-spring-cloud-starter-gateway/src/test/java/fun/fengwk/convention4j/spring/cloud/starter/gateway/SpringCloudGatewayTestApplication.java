package fun.fengwk.convention4j.spring.cloud.starter.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author fengwk
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SpringCloudGatewayTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayTestApplication.class, args);
    }

}
