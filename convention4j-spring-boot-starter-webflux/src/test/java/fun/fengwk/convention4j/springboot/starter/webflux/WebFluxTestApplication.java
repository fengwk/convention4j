package fun.fengwk.convention4j.springboot.starter.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author fengwk
 */
@Slf4j
@SpringBootApplication
public class WebFluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxTestApplication.class, args);
    }

}
