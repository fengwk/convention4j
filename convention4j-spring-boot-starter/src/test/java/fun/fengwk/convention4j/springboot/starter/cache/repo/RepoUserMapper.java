package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserPO;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface RepoUserMapper extends BaseMapper {

    int insert(UserPO record);

    int insertAll(Collection<UserPO> records);

    int deleteById(Long id);

    int deleteByIdIn(Collection<Long> id);

    int updateByIdSelective(UserPO record);

    UserPO findById(Long id);

    List<UserPO> findByIdIn(Collection<Long> ids);

    List<UserPO> findByAgeOrderByIdDesc(int age);

}
