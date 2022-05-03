package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.common.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author fengwk
 */
@Validated
@AutoResultExceptionHandler
@Component
public class TestService {

    public Result<String> test1() {
        int a = 1 / 0;
        return Results.success("ok");
    }

    public String test2() {
        int a = 1 / 0;
        return "ok";
    }

    public Result<Void> checkUser(@Valid User user) {
        return Results.success();
    }

    static class User {

        @NotNull
        private Long id;

    }

}
