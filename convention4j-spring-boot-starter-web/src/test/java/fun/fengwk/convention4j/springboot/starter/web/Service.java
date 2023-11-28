package fun.fengwk.convention4j.springboot.starter.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author fengwk
 */
@Validated
@Component
public class Service {

    public void hello3(@Valid Data data) {}

}
