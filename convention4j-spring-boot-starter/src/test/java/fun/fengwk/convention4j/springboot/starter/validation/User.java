package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.api.validation.group.FullUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class User {

    @NotNull
    private Long id;
    @NotEmpty(groups = FullUpdate.class)
    private String name;
    private String desc;

}
