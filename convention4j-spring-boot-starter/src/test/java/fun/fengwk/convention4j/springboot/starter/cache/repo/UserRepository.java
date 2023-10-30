package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author fengwk
 */
@CacheSupport(objClass = UserPO.class)
@AllArgsConstructor
@Repository
public class UserRepository {

    private final RepoUserMapper repoUserMapper;

    @WriteMethod
    public boolean add(@EvictObject UserPO userDO) {
        return repoUserMapper.insert(userDO) > 0;
    }

    @WriteMethod
    public boolean addAll(@EvictObject Collection<UserPO> userDOs) {
        return repoUserMapper.insertAll(userDOs) == userDOs.size();
    }

    @WriteMethod(objQueryMethod = "doListByIds")
    public boolean updateByIdSelective(@EvictIndex("id") UserPO userDO) {
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
    public UserPO getById(@ListenKey("id") long id) {
        return repoUserMapper.findById(id);
    }

    @ReadMethod
    public List<UserPO> listByIds(@ListenKey("id") Collection<Long> ids) {
        return repoUserMapper.findByIdIn(ids);
    }

    @ReadMethod
    public List<UserPO> listByAgeOrderByIdDesc(@ListenKey("age") int age) {
        return repoUserMapper.findByAgeOrderByIdDesc(age);
    }

    @ReadMethod
    public Set<UserPO> listByAgeOrderByIdDescSet(@ListenKey("age") int age) {
        return new HashSet<>(repoUserMapper.findByAgeOrderByIdDesc(age));
    }

    @ReadMethod
    public LinkedList<UserPO> listByAgeOrderByIdDescLinkedList(@ListenKey("age") int age) {
        return new LinkedList<>(repoUserMapper.findByAgeOrderByIdDesc(age));
    }

    @ReadMethod
    public List<UserPO> listByAgeOrderByIdDesc(UserNameAge userNameAge) {
        return repoUserMapper.findByAgeOrderByIdDesc(userNameAge.getAge());
    }

    public List<UserPO> doListByIds(Collection<Long> ids) {
        return repoUserMapper.findByIdIn(ids);
    }

    @Data
    public static class UserNameAge {

        private int age;
        private String username;

    }

}
