package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.api.validation.group.FullUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author fengwk
 */
@Validated // 必须有类级别的@Validated注解
@Service
public class UserService {

    @Validated({Default.class, FullUpdate.class})
    public void checkUser(@NotNull @Valid User user) {
        System.out.println(user);
    }

}
