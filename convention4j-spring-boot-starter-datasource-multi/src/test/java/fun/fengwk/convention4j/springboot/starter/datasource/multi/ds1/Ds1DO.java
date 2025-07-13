package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1;

import fun.fengwk.convention4j.springboot.starter.persistence.ConventionDO;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class Ds1DO extends ConventionDO<Long> {

    private String name;

}
