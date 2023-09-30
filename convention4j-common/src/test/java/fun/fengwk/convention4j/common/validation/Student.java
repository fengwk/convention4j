package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.api.validation.Checker;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author fengwk
 */
@Checker
@Data
public class Student {

    private Long id;
    @Checker
    private String name;

}
