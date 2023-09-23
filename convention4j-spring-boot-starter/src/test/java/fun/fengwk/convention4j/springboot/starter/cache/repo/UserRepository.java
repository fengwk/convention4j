package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserPO;
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
public class UserRepository implements GsonLongIdCacheSupport<UserPO> {

    private final RepoUserMapper userMapper;

    @CacheWriteMethod
    public boolean add(UserPO userDO) {
        return userMapper.insert(userDO) > 0;
    }

    @CacheWriteMethod
    public boolean addAll(Collection<UserPO> userDOs) {
        return userMapper.insertAll(userDOs) == userDOs.size();
    }

    @CacheWriteMethod
    public boolean updateByIdSelective(UserPO userDO) {
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
    public UserPO getById(@IdKey("id") long id) {
        return userMapper.findById(id);
    }

    @CacheReadMethod(useIdQuery = true)
    public List<UserPO> listByIds(@IdKey("id") Collection<Long> ids) {
        return userMapper.findByIdIn(ids);
    }

    @CacheReadMethod
    public List<UserPO> listByAgeOrderByIdDesc(@Key("age") int age) {
        return userMapper.findByAgeOrderByIdDesc(age);
    }

    @CacheReadMethod
    public Set<UserPO> listByAgeOrderByIdDescSet(@Key("age") int age) {
        return new HashSet<>(userMapper.findByAgeOrderByIdDesc(age));
    }

    @CacheReadMethod
    public LinkedList<UserPO> listByAgeOrderByIdDescLinkedList(@Key("age") int age) {
        return new LinkedList<>(userMapper.findByAgeOrderByIdDesc(age));
    }

    @CacheReadMethod
    public List<UserPO> listByAgeOrderByIdDesc(UserNameAge userNameAge) {
        return userMapper.findByAgeOrderByIdDesc(userNameAge.getAge());
    }

    @Override
    public List<UserPO> doListByIds(Collection<Long> ids) {
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
