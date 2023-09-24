package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.api.validation.EnumName;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author fengwk
 */
@Data
public class TestBean {

    @EnumName(TestType.class)
    private String type;

    @EnumName(TestType.class)
    private List<String> typeList;

    @EnumName(TestType.class)
    private Set<String> typeSet;

}
