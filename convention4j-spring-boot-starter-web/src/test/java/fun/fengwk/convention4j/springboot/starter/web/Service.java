package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.springboot.starter.tracer.ConventionSpan;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author fengwk
 */
@Validated
@Component
public class Service {

    @ConventionSpan(alias = "你好")
    public void hello() {
        System.out.println("123");
    }

    public void hello3(@Valid Data data) {}

}
