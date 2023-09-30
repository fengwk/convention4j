package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.api.validation.Checker;
import lombok.Data;

/**
 * @author fengwk
 */
@Checker
@Data
public class Teach {

    private Long id;
    private String name;

}
