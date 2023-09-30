package fun.fengwk.convention4j.common.validation.group;

import fun.fengwk.convention4j.api.validation.group.FullUpdate;
import fun.fengwk.convention4j.api.validation.group.PartUpdate;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
