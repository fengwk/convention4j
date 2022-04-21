package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.api.result.Results;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
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

}
