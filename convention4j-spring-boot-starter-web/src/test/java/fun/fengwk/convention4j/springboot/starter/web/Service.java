package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.springboot.starter.tracer.ConventionSpan;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author fengwk
 */
@Slf4j
@Validated
@Component
public class Service {

    @ConventionSpan(alias = "你好")
    public void hello() {
        System.out.println("123");
        log.info("sub service");
    }

    public void hello3(@Valid Data data) {}

}
