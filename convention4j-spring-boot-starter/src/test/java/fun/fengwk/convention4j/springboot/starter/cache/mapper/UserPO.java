package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.persistence.BasePO;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class UserPO extends BasePO<Long> {

    private String username;
    private String email;
    private String mobile;
    private String password;
    private Integer age;
    private String city;

}
