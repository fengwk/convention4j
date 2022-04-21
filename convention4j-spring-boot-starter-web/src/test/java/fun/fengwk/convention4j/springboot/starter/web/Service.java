package fun.fengwk.convention4j.springboot.starter.web;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * @author fengwk
 */
@Validated
@Component
public class Service {

    public void hello3(@Valid Data data) {}

}
