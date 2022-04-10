package fun.fengwk.convention.springboot.test.starter.mysql;

import fun.fengwk.automapper.annotation.AutoMapper;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface UserMapper {

    int insert(UserDO userDO);

    UserDO findById(long id);

    List<UserDO> findAll();

    List<UserDO> findByUsernameStartingWith(String username);

}
