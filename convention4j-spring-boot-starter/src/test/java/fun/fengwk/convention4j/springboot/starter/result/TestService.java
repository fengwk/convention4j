package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author fengwk
 */
@Validated
@AutoResultExceptionHandler
@Component
public class TestService {

    public Result<String> test1() {
        int a = 1 / 0;
        return Results.ok();
    }

    public String test2() {
        int a = 1 / 0;
        return "ok";
    }

    public Result<Void> checkUser(@Valid User user) {
        return Results.ok();
    }

    static class User {

        @NotNull
        private Long id;

    }

}
