package fun.fengwk.convention4j.spring.cloud.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 
 * @author fengwk
 */
@Slf4j
@EnableFeignClients
@SpringBootApplication
public class SpringCloudTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudTestApplication.class, args);
    }

}
