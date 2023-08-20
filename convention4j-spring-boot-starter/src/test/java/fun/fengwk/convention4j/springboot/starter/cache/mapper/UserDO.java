package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author fengwk
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class UserDO extends BaseCacheDO<Long> {

    @Key
    private String username;

    @Key
    private String email;

    @Key
    private String mobile;

    private String password;

    @Key
    private Integer age;

    @Key
    private String city;

}
