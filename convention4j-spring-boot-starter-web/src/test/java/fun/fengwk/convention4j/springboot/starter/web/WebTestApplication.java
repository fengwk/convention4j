package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.test.starter.redis.EnableEmbeddedRedisServer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * @author fengwk
 */
//@Import({org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
//org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration.class
//})
@Slf4j
@EnableEmbeddedRedisServer
@Validated
@RestController
@SpringBootApplication
public class WebTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebTestApplication.class, args);
    }

    @Autowired
    private Service service;
    
    @GetMapping("/hello")
    public Result<String> hello(@RequestParam("id") @Max(15) long id) {
        log.info("hello");
        new Thread(() -> {
            try {
                service.hello();
                log.info("sub hello");
                throw new IllegalStateException("123");
            } catch (Exception ex) {
                log.error("err", ex);
            }
        }).start();
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        executorService.submit(() -> {
//            try {
//                log.info("sub hello");
//                throw new IllegalStateException("123");
//            } catch (Exception ex) {
//                log.error("err", ex);
//            }
//        });
        return Results.ok("hello");
    }

    @PostMapping("/hello2")
    public Result<String> hello2(@RequestBody @Valid Data data) {
        return Results.created("hello");
    }

    @PostMapping("/hello3")
    public Result<String> hello3(@RequestBody Data data) {
        service.hello3(data);
        return Results.ok("hello");
    }

    @RequestMapping("/hello4")
    public Result<String> hello4(@RequestParam("uri") String uri) {
        return Results.ok(uri);
    }

}
