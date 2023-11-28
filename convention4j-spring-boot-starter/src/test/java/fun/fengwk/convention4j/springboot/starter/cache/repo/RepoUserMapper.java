package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserDO;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface RepoUserMapper extends BaseMapper {

    int insert(UserDO record);

    int insertAll(Collection<UserDO> records);

    int deleteById(Long id);

    int deleteByIdIn(Collection<Long> id);

    int updateByIdSelective(UserDO record);

    UserDO findById(Long id);

    List<UserDO> findByIdIn(Collection<Long> ids);

    List<UserDO> findByAgeOrderByIdDesc(int age);

}
