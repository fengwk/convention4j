package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author fengwk
 */
@CacheSupport(objClass = UserDO.class)
@AllArgsConstructor
@Repository
public class UserRepository {

    private final RepoUserMapper repoUserMapper;

    @WriteMethod
    public boolean add(@EvictObject UserDO userDO) {
        return repoUserMapper.insert(userDO) > 0;
    }

    @WriteMethod
    public boolean addAll(@EvictObject Collection<UserDO> userDOs) {
        return repoUserMapper.insertAll(userDOs) == userDOs.size();
    }

    @WriteMethod(objQueryMethod = "doListByIds")
    public boolean updateByIdSelective(@EvictIndex("id") UserDO userDO) {
        return repoUserMapper.updateByIdSelective(userDO) > 0;
    }

    @WriteMethod(objQueryMethod = "doListByIds")
    public boolean deleteById(@EvictIndex long id) {
        return repoUserMapper.deleteById(id) > 0;
    }

    @WriteMethod(objQueryMethod = "doListByIds")
    public boolean deleteByIds(@EvictIndex Collection<Long> ids) {
        return repoUserMapper.deleteByIdIn(ids) > 0;
    }

    @ReadMethod
    public UserDO getById(@ListenKey("id") long id) {
        return repoUserMapper.findById(id);
    }

    @ReadMethod
    public List<UserDO> listByIds(@ListenKey("id") Collection<Long> ids) {
        return repoUserMapper.findByIdIn(ids);
    }

    @ReadMethod
    public List<UserDO> listByAgeOrderByIdDesc(@ListenKey("age") int age) {
        return repoUserMapper.findByAgeOrderByIdDesc(age);
    }

    @ReadMethod
    public Set<UserDO> listByAgeOrderByIdDescSet(@ListenKey("age") int age) {
        return new HashSet<>(repoUserMapper.findByAgeOrderByIdDesc(age));
    }

    @ReadMethod
    public LinkedList<UserDO> listByAgeOrderByIdDescLinkedList(@ListenKey("age") int age) {
        return new LinkedList<>(repoUserMapper.findByAgeOrderByIdDesc(age));
    }

    @ReadMethod
    public List<UserDO> listByAgeOrderByIdDesc(UserNameAge userNameAge) {
        return repoUserMapper.findByAgeOrderByIdDesc(userNameAge.getAge());
    }

    public List<UserDO> doListByIds(Collection<Long> ids) {
        return repoUserMapper.findByIdIn(ids);
    }

    @Data
    public static class UserNameAge {

        private int age;
        private String username;

    }

}
