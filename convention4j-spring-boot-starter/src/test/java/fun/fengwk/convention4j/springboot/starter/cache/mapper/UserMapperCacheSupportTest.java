package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
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
public class UserMapperCacheSupportTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGen;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransactionTest transactionTest;
    @Autowired
    private CacheSupportMetrics cacheSupportMetrics;
//    @Autowired
//    private CacheAdapterMetrics cacheAdapterMetrics;

    @Test
    public void test() throws NoSuchMethodException {
        Method methodInsert = UserMapper.class.getMethod("insert", UserPO.class);
        Method methodInertAll = UserMapper.class.getMethod("insertAll", Collection.class);
        Method methodUpdateByIdSelective = UserMapper.class.getMethod("updateByIdSelective", UserPO.class);
        Method methodFindById = UserMapper.class.getMethod("findById", Long.class);
        Method methodFindByAgeOrderByIdDesc = UserMapper.class.getMethod("findByAgeOrderByIdDesc", int.class);
        Method methodFindByIdIn = UserMapper.class.getMethod("findByIdIn", Collection.class);
        Method methodDeleteById = UserMapper.class.getMethod("deleteById", Long.class);
        Method methodDeleteByIdIn = UserMapper.class.getMethod("deleteByIdIn", Collection.class);

        UserPO userPO1 = new UserPO();
        userPO1.setUsername("username");
        userPO1.setEmail("email");
        userPO1.setMobile("mobile");
        userPO1.setPassword("password");
        userPO1.setAge(18);
        userPO1.setCity("hangzhou");
        userPO1.setId(idGen.next(getClass()));
        assert userMapper.insert(userPO1) > 0;
        assert cacheSupportMetrics.getCallCount(methodInsert) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;

        UserPO found = userMapper.findById(userPO1.getId());
        assert cacheSupportMetrics.getCallCount(methodInsert) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;
        assert Objects.equals(userPO1, found);

        UserPO updatePO = new UserPO();
        updatePO.setId(userPO1.getId());
        updatePO.setPassword("password_update");
        assert userMapper.updateByIdSelective(updatePO) > 0;
        assert cacheSupportMetrics.getCallCount(methodUpdateByIdSelective) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodUpdateByIdSelective) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodUpdateByIdSelective) == 1L;

        found = userMapper.findById(userPO1.getId());
        assert cacheSupportMetrics.getCallCount(methodFindById) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindById) == 0L;
        assert Objects.equals(updatePO.getPassword(), found.getPassword());

        userMapper.findById(userPO1.getId());
        assert cacheSupportMetrics.getCallCount(methodFindById) == 3L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindById) == 1L;

        UserPO userPO2 = new UserPO();
        userPO2.setUsername("username_2");
        userPO2.setEmail("email_2");
        userPO2.setMobile("mobile_2");
        userPO2.setPassword("password_2");
        userPO2.setAge(18);
        userPO2.setCity("hangzhou");
        userPO2.setId(idGen.next(getClass()));
        assert userMapper.insert(userPO2) > 0;
        assert cacheSupportMetrics.getCallCount(methodInsert) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;

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
        assert userMapper.insertAll(user34List) > 0;
        assert cacheSupportMetrics.getCallCount(methodInertAll) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInertAll) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInertAll) == 0L;

        userMapper.findById(userPO1.getId());
        assert cacheSupportMetrics.getCallCount(methodFindById) == 4L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindById) == 2L;

        assert userMapper.findByAgeOrderByIdDesc(18).size() == 2;
        assert cacheSupportMetrics.getCallCount(methodFindByAgeOrderByIdDesc) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByAgeOrderByIdDesc) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByAgeOrderByIdDesc) == 0L;
        assert userMapper.findByAgeOrderByIdDesc(18).size() == 2;
        assert cacheSupportMetrics.getCallCount(methodFindByAgeOrderByIdDesc) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByAgeOrderByIdDesc) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByAgeOrderByIdDesc) == 1L;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 0L;
        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteById(userPO1.getId()) > 0;
        assert cacheSupportMetrics.getCallCount(methodDeleteById) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodDeleteById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodDeleteById) == 1L;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 3;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 3L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 2L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO4.getId())) > 0;
        assert cacheSupportMetrics.getCallCount(methodDeleteByIdIn) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodDeleteByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodDeleteByIdIn) == 0L;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 1;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 4L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 3L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        transactionTest.test();

        long id5 = idGen.next(getClass());
        assert userMapper.countById(id5) == 0;
        UserPO userPO5 = new UserPO();
        userPO5.setUsername("username_5");
        userPO5.setEmail("email_5");
        userPO5.setMobile("mobile_5");
        userPO5.setPassword("password_5");
        userPO5.setAge(55);
        userPO5.setCity("hangzhou");
        userPO5.setId(id5);
        assert userMapper.insert(userPO5) > 0;
        assert userMapper.countById(id5) > 0;

        assert userMapper.countByAge(55) == 1;
        UserPO userPO6 = new UserPO();
        userPO6.setUsername("username_6");
        userPO6.setEmail("email_6");
        userPO6.setMobile("mobile_6");
        userPO6.setPassword("password_6");
        userPO6.setAge(55);
        userPO6.setCity("hangzhou");
        userPO6.setId(idGen.next(getClass()));
        assert userMapper.insert(userPO6) > 0;
        assert userMapper.countByAge(55) == 2;

//        System.out.println(cacheAdapterMetrics.getReadCount());
//        System.out.println(cacheAdapterMetrics.getWriteCount());
//        System.out.println(cacheAdapterMetrics.getWriteKeyCount());
    }

}
