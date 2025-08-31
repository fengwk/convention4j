package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.common.web.CookieUtils;
import fun.fengwk.convention4j.springboot.test.starter.redis.EnableEmbeddedRedisServer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @RequestMapping("/hello5")
    public Result<String> hello5() {
        return Results.error(CommonErrorCodes.NOT_FOUND);
    }

    @RequestMapping("/hello6")
    public Result<String> hello6() {
        throw CommonErrorCodes.NOT_FOUND.asThrowable();
    }

    @RequestMapping("/cookie/set")
    public Result<Void> testCookieSet(HttpServletResponse response) {
        CookieUtils.setCookie(response, "my-cookie", "test-my-cookie", 600, false);
        return Results.ok();
    }

    @RequestMapping("/cookie/get")
    public Result<Cookie> testCookieGet(HttpServletRequest request) {
        return Results.ok(CookieUtils.getCookie(request, "my-cookie"));
    }

    @RequestMapping("/cookie/del")
    public Result<Void> testCookieDel(HttpServletResponse response) {
        CookieUtils.deleteCookie(response, "my-cookie");
        return Results.ok();
    }

}
