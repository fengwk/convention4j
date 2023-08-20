package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;

/**
 * 
 * @author fengwk
 */
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
    
}
