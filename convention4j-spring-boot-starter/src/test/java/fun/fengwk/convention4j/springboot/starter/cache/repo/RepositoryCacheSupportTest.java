package fun.fengwk.convention4j.springboot.starter.cache.repo;

import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.UserDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    private CacheManagerMetrics cacheManagerMetrics;

    @Test
    public void test() throws NoSuchMethodException {
        String methodAdd = UserRepository.class.getMethod("add", UserDO.class).toString();
        String methodAddAll = UserRepository.class.getMethod("addAll", Collection.class).toString();
        String methodUpdateByIdSelective = UserRepository.class.getMethod("updateByIdSelective", UserDO.class).toString();
        String methodGetById = UserRepository.class.getMethod("getById", long.class).toString();
        String methodListByAgeOrderByIdDesc = UserRepository.class.getMethod("listByAgeOrderByIdDesc", int.class).toString();
        String methodListByAgeOrderByIdDescSet = UserRepository.class.getMethod("listByAgeOrderByIdDescSet", int.class).toString();
        String methodListByAgeOrderByIdDescLinkedList = UserRepository.class.getMethod("listByAgeOrderByIdDescLinkedList", int.class).toString();
        String methodListByAgeOrderByIdDescUA = UserRepository.class.getMethod("listByAgeOrderByIdDesc", UserRepository.UserNameAge.class).toString();
        String methodListByIds = UserRepository.class.getMethod("listByIds", Collection.class).toString();
        String methodDeleteById = UserRepository.class.getMethod("deleteById", long.class).toString();
        String methodDeleteByIds = UserRepository.class.getMethod("deleteByIds", Collection.class).toString();

        UserDO userPO1 = new UserDO();
        userPO1.setUsername("username");
        userPO1.setEmail("email");
        userPO1.setMobile("mobile");
        userPO1.setPassword("password");
        userPO1.setAge(18);
        userPO1.setCity("hangzhou");
        userPO1.setId(idGen.next(getClass()));
        assert userRepository.add(userPO1);

        UserDO found = userRepository.getById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodGetById) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodGetById) == 0L;
        assert Objects.equals(userPO1, found);

        UserDO updatePO = new UserDO();
        updatePO.setId(userPO1.getId());
        updatePO.setPassword("password_update");
        assert userRepository.updateByIdSelective(updatePO);

        found = userRepository.getById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodGetById) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodGetById) == 0L;
        assert Objects.equals(updatePO.getPassword(), found.getPassword());

        userRepository.getById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodGetById) == 3L;
        assert cacheManagerMetrics.getReadHitCount(methodGetById) == 1L;

        UserDO userPO2 = new UserDO();
        userPO2.setUsername("username_2");
        userPO2.setEmail("email_2");
        userPO2.setMobile("mobile_2");
        userPO2.setPassword("password_2");
        userPO2.setAge(18);
        userPO2.setCity("hangzhou");
        userPO2.setId(idGen.next(getClass()));
        assert userRepository.add(userPO2);

        UserDO userPO3 = new UserDO();
        userPO3.setUsername("username_3");
        userPO3.setEmail("email_3");
        userPO3.setMobile("mobile_3");
        userPO3.setPassword("password_3");
        userPO3.setAge(20);
        userPO3.setCity("hangzhou");
        userPO3.setId(idGen.next(getClass()));
        UserDO userPO4 = new UserDO();
        userPO4.setUsername("username_4");
        userPO4.setEmail("email_4");
        userPO4.setMobile("mobile_4");
        userPO4.setPassword("password_4");
        userPO4.setAge(19);
        userPO4.setCity("hangzhou");
        userPO4.setId(idGen.next(getClass()));
        List<UserDO> user34List = Arrays.asList(userPO3, userPO4);
        assert userRepository.addAll(user34List);

        userRepository.getById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodGetById) == 4L;
        assert cacheManagerMetrics.getReadHitCount(methodGetById) == 2L;

        assert userRepository.listByAgeOrderByIdDesc(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDesc) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDesc) == 0L;
        assert userRepository.listByAgeOrderByIdDesc(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDesc) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDesc) == 1L;

        assert userRepository.listByAgeOrderByIdDescSet(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescSet) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescSet) == 0L;
        assert userRepository.listByAgeOrderByIdDescSet(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescSet) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescSet) == 1L;

        assert userRepository.listByAgeOrderByIdDescLinkedList(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescLinkedList) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescLinkedList) == 0L;
        assert userRepository.listByAgeOrderByIdDescLinkedList(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescLinkedList) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescLinkedList) == 1L;

        UserRepository.UserNameAge userNameAge = new UserRepository.UserNameAge();
        userNameAge.setAge(18);
        assert userRepository.listByAgeOrderByIdDesc(userNameAge).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescUA) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescUA) == 0L;
        assert userRepository.listByAgeOrderByIdDesc(userNameAge).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodListByAgeOrderByIdDescUA) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodListByAgeOrderByIdDescUA) == 1L;

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheManagerMetrics.getReadCount(methodListByIds) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodListByIds) == 0L;
        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheManagerMetrics.getReadCount(methodListByIds) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodListByIds) == 1L;

        assert userRepository.deleteById(userPO1.getId());

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 3;
        assert cacheManagerMetrics.getReadCount(methodListByIds) == 3L;
        assert cacheManagerMetrics.getReadHitCount(methodListByIds) == 1L;

        assert userRepository.deleteByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO4.getId()));

        assert userRepository.listByIds(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 1;
        assert cacheManagerMetrics.getReadCount(methodListByIds) == 4L;
        assert cacheManagerMetrics.getReadHitCount(methodListByIds) == 1L;
    }

}
