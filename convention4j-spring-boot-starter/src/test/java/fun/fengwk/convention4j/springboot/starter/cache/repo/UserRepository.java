package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserDO;
import fun.fengwk.convention4j.springboot.starter.cache.support.GsonLongIdCacheSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author fengwk
 */
@CacheConfig(version = "v1")
@AllArgsConstructor
@Repository
public class UserRepository implements GsonLongIdCacheSupport<UserDO> {

    private final RepoUserMapper userMapper;

    @CacheWriteMethod
    public boolean add(UserDO userDO) {
        return userMapper.insert(userDO) > 0;
    }

    @CacheWriteMethod
    public boolean addAll(Collection<UserDO> userDOs) {
        return userMapper.insertAll(userDOs) == userDOs.size();
    }

    @CacheWriteMethod
    public boolean updateByIdSelective(UserDO userDO) {
        return userMapper.updateByIdSelective(userDO) > 0;
    }

    @CacheWriteMethod
    public boolean deleteById(@IdKey("id") long id) {
        return userMapper.deleteById(id) > 0;
    }

    @CacheWriteMethod
    public boolean deleteByIds(@IdKey("id") Collection<Long> ids) {
        return userMapper.deleteByIdIn(ids) > 0;
    }

    @CacheReadMethod(useIdQuery = true)
    public UserDO getById(@IdKey("id") long id) {
        return userMapper.findById(id);
    }

    @CacheReadMethod(useIdQuery = true)
    public List<UserDO> listByIds(@IdKey("id") Collection<Long> ids) {
        return userMapper.findByIdIn(ids);
    }

    @CacheReadMethod
    public List<UserDO> listByAgeOrderByIdDesc(@Key("age") int age) {
        return userMapper.findByAgeOrderByIdDesc(age);
    }

    @CacheReadMethod
    public Set<UserDO> listByAgeOrderByIdDescSet(@Key("age") int age) {
        return new HashSet<>(userMapper.findByAgeOrderByIdDesc(age));
    }

    @CacheReadMethod
    public LinkedList<UserDO> listByAgeOrderByIdDescLinkedList(@Key("age") int age) {
        return new LinkedList<>(userMapper.findByAgeOrderByIdDesc(age));
    }

    @CacheReadMethod
    public List<UserDO> listByAgeOrderByIdDesc(UserNameAge userNameAge) {
        return userMapper.findByAgeOrderByIdDesc(userNameAge.getAge());
    }

    @Override
    public List<UserDO> doListByIds(Collection<Long> ids) {
        return userMapper.findByIdIn(ids);
    }

    @Data
    public static class UserNameAge {
        @Key
        private int age;
        @Key
        private String username;
    }

}
