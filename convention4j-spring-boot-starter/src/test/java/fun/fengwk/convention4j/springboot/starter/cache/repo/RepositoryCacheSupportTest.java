package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserPO;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.CacheSupportMetrics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class RepositoryCacheSupportTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGen;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheSupportMetrics cacheRepoMetrics;

    @Test
    public void test() throws NoSuchMethodException {
        Method methodAdd = UserRepository.class.getMethod("add", UserPO.class);
        Method methodAddAll = UserRepository.class.getMethod("addAll", Collection.class);
        Method methodUpdateByIdSelective = UserRepository.class.getMethod("updateByIdSelective", UserPO.class);
        Method methodGetById = UserRepository.class.getMethod("getById", long.class);
        Method methodListByAgeOrderByIdDesc = UserRepository.class.getMethod("listByAgeOrderByIdDesc", int.class);
        Method methodListByAgeOrderByIdDescSet = UserRepository.class.getMethod("listByAgeOrderByIdDescSet", int.class);
        Method methodListByAgeOrderByIdDescLinkedList = UserRepository.class.getMethod("listByAgeOrderByIdDescLinkedList", int.class);
        Method methodListByAgeOrderByIdDescUA = UserRepository.class.getMethod("listByAgeOrderByIdDesc", UserRepository.UserNameAge.class);
        Method methodListByIds = UserRepository.class.getMethod("listByIds", Collection.class);
        Method methodDeleteById = UserRepository.class.getMethod("deleteById", long.class);
        Method methodDeleteByIds = UserRepository.class.getMethod("deleteByIds", Collection.class);

        UserPO userPO1 = new UserPO();
        userPO1.setUsername("username");
        userPO1.setEmail("email");
        userPO1.setMobile("mobile");
        userPO1.setPassword("password");
        userPO1.setAge(18);
        userPO1.setCity("hangzhou");
        userPO1.setId(idGen.next(getClass()));
        assert userRepository.add(userPO1);
        assert cacheRepoMetrics.getCallCount(methodAdd) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodAdd) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodAdd) == 0L;

        UserPO found = userRepository.getById(userPO1.getId());
        assert cacheRepoMetrics.getCallCount(methodAdd) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodAdd) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodAdd) == 0L;
        assert Objects.equals(userPO1, found);

        UserPO updatePO = new UserPO();
        updatePO.setId(userPO1.getId());
        updatePO.setPassword("password_update");
        assert userRepository.updateByIdSelective(updatePO);
        assert cacheRepoMetrics.getCallCount(methodUpdateByIdSelective) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodUpdateByIdSelective) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodUpdateByIdSelective) == 1L;

        found = userRepository.getById(userPO1.getId());
        assert cacheRepoMetrics.getCallCount(methodGetById) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodGetById) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodGetById) == 0L;
        assert Objects.equals(updatePO.getPassword(), found.getPassword());

        userRepository.getById(userPO1.getId());
        assert cacheRepoMetrics.getCallCount(methodGetById) == 3L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodGetById) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodGetById) == 1L;

        UserPO userPO2 = new UserPO();
        userPO2.setUsername("username_2");
        userPO2.setEmail("email_2");
        userPO2.setMobile("mobile_2");
        userPO2.setPassword("password_2");
        userPO2.setAge(18);
        userPO2.setCity("hangzhou");
        userPO2.setId(idGen.next(getClass()));
        assert userRepository.add(userPO2);
        assert cacheRepoMetrics.getCallCount(methodAdd) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodAdd) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodAdd) == 0L;

        UserPO userPO3 = new UserPO();
        userPO3.setUsername("username_3");
        userPO3.setEmail("email_3");
        userPO3.setMobile("mobile_3");
        userPO3.setPassword("password_3");
        userPO3.setAge(20);
        userPO3.setCity("hangzhou");
        userPO3.setId(idGen.next(getClass()));
        UserPO userPO4 = new UserPO();
        userPO4.setUsername("username_4");
        userPO4.setEmail("email_4");
        userPO4.setMobile("mobile_4");
        userPO4.setPassword("password_4");
        userPO4.setAge(19);
        userPO4.setCity("hangzhou");
        userPO4.setId(idGen.next(getClass()));
        List<UserPO> user34List = Arrays.asList(userPO3, userPO4);
        assert userRepository.addAll(user34List);
        assert cacheRepoMetrics.getCallCount(methodAddAll) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodAddAll) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodAddAll) == 0L;

        userRepository.getById(userPO1.getId());
        assert cacheRepoMetrics.getCallCount(methodGetById) == 4L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodGetById) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodGetById) == 2L;

        assert userRepository.listByAgeOrderByIdDesc(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDesc) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDesc) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDesc) == 0L;
        assert userRepository.listByAgeOrderByIdDesc(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDesc) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDesc) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDesc) == 1L;

        assert userRepository.listByAgeOrderByIdDescSet(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescSet) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescSet) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescSet) == 0L;
        assert userRepository.listByAgeOrderByIdDescSet(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescSet) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescSet) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescSet) == 1L;

        assert userRepository.listByAgeOrderByIdDescLinkedList(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescLinkedList) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescLinkedList) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescLinkedList) == 0L;
        assert userRepository.listByAgeOrderByIdDescLinkedList(18).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescLinkedList) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescLinkedList) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescLinkedList) == 1L;

        UserRepository.UserNameAge userNameAge = new UserRepository.UserNameAge();
        userNameAge.setAge(18);
        assert userRepository.listByAgeOrderByIdDesc(userNameAge).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescUA) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescUA) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescUA) == 0L;
        assert userRepository.listByAgeOrderByIdDesc(userNameAge).size() == 2;
        assert cacheRepoMetrics.getCallCount(methodListByAgeOrderByIdDescUA) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByAgeOrderByIdDescUA) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByAgeOrderByIdDescUA) == 1L;

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheRepoMetrics.getCallCount(methodListByIds) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByIds) == 1L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByIds) == 0L;
        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheRepoMetrics.getCallCount(methodListByIds) == 2L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByIds) == 1L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByIds) == 1L;

        assert userRepository.deleteById(userPO1.getId());
        assert cacheRepoMetrics.getCallCount(methodDeleteById) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodDeleteById) == 0L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodDeleteById) == 1L;

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 3;
        assert cacheRepoMetrics.getCallCount(methodListByIds) == 3L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByIds) == 2L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByIds) == 1L;

        assert userRepository.deleteByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO4.getId()));
        assert cacheRepoMetrics.getCallCount(methodDeleteByIds) == 1L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodDeleteByIds) == 1L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodDeleteByIds) == 0L;

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 1;
        assert cacheRepoMetrics.getCallCount(methodListByIds) == 4L;
        assert cacheRepoMetrics.getPartialCacheHitCount(methodListByIds) == 3L;
        assert cacheRepoMetrics.getFullCacheHitCount(methodListByIds) == 1L;
    }

}
