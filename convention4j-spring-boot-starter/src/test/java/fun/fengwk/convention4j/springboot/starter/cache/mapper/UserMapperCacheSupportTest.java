package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.CacheAdapterMetrics;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.CacheSupportMetrics;
import org.checkerframework.checker.units.qual.A;
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
        Method methodInsert = UserMapper.class.getMethod("insert", UserDO.class);
        Method methodInertAll = UserMapper.class.getMethod("insertAll", Collection.class);
        Method methodUpdateByIdSelective = UserMapper.class.getMethod("updateByIdSelective", UserDO.class);
        Method methodFindById = UserMapper.class.getMethod("findById", Long.class);
        Method methodFindByAgeOrderByIdDesc = UserMapper.class.getMethod("findByAgeOrderByIdDesc", int.class);
        Method methodFindByIdIn = UserMapper.class.getMethod("findByIdIn", Collection.class);
        Method methodDeleteById = UserMapper.class.getMethod("deleteById", Long.class);
        Method methodDeleteByIdIn = UserMapper.class.getMethod("deleteByIdIn", Collection.class);

        UserDO userDO1 = new UserDO();
        userDO1.setUsername("username");
        userDO1.setEmail("email");
        userDO1.setMobile("mobile");
        userDO1.setPassword("password");
        userDO1.setAge(18);
        userDO1.setCity("hangzhou");
        userDO1.setId(idGen.next(getClass()));
        assert userMapper.insert(userDO1) > 0;
        assert cacheSupportMetrics.getCallCount(methodInsert) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;

        UserDO found = userMapper.findById(userDO1.getId());
        assert cacheSupportMetrics.getCallCount(methodInsert) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;
        assert Objects.equals(userDO1, found);

        UserDO updateDO = new UserDO();
        updateDO.setId(userDO1.getId());
        updateDO.setPassword("password_update");
        assert userMapper.updateByIdSelective(updateDO) > 0;
        assert cacheSupportMetrics.getCallCount(methodUpdateByIdSelective) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodUpdateByIdSelective) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodUpdateByIdSelective) == 1L;

        found = userMapper.findById(userDO1.getId());
        assert cacheSupportMetrics.getCallCount(methodFindById) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindById) == 0L;
        assert Objects.equals(updateDO.getPassword(), found.getPassword());

        userMapper.findById(userDO1.getId());
        assert cacheSupportMetrics.getCallCount(methodFindById) == 3L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindById) == 1L;

        UserDO userDO2 = new UserDO();
        userDO2.setUsername("username_2");
        userDO2.setEmail("email_2");
        userDO2.setMobile("mobile_2");
        userDO2.setPassword("password_2");
        userDO2.setAge(18);
        userDO2.setCity("hangzhou");
        userDO2.setId(idGen.next(getClass()));
        assert userMapper.insert(userDO2) > 0;
        assert cacheSupportMetrics.getCallCount(methodInsert) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInsert) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInsert) == 0L;

        UserDO userDO3 = new UserDO();
        userDO3.setUsername("username_3");
        userDO3.setEmail("email_3");
        userDO3.setMobile("mobile_3");
        userDO3.setPassword("password_3");
        userDO3.setAge(20);
        userDO3.setCity("hangzhou");
        userDO3.setId(idGen.next(getClass()));
        UserDO userDO4 = new UserDO();
        userDO4.setUsername("username_4");
        userDO4.setEmail("email_4");
        userDO4.setMobile("mobile_4");
        userDO4.setPassword("password_4");
        userDO4.setAge(19);
        userDO4.setCity("hangzhou");
        userDO4.setId(idGen.next(getClass()));
        List<UserDO> user34List = Arrays.asList(userDO3, userDO4);
        assert userMapper.insertAll(user34List) > 0;
        assert cacheSupportMetrics.getCallCount(methodInertAll) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodInertAll) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodInertAll) == 0L;

        userMapper.findById(userDO1.getId());
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

        assert userMapper.findByIdIn(Arrays.asList(userDO1.getId(), userDO2.getId(), userDO3.getId(), userDO4.getId())).size() == 4;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 0L;
        assert userMapper.findByIdIn(Arrays.asList(userDO1.getId(), userDO2.getId(), userDO3.getId(), userDO4.getId())).size() == 4;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 2L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteById(userDO1.getId()) > 0;
        assert cacheSupportMetrics.getCallCount(methodDeleteById) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodDeleteById) == 0L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodDeleteById) == 1L;

        assert userMapper.findByIdIn(Arrays.asList(userDO1.getId(), userDO2.getId(), userDO3.getId(), userDO4.getId())).size() == 3;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 3L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 2L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteByIdIn(Arrays.asList(userDO1.getId(), userDO2.getId(), userDO4.getId())) > 0;
        assert cacheSupportMetrics.getCallCount(methodDeleteByIdIn) == 1L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodDeleteByIdIn) == 1L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodDeleteByIdIn) == 0L;

        assert userMapper.findByIdIn(Arrays.asList(userDO1.getId(), userDO2.getId(), userDO3.getId(), userDO4.getId())).size() == 1;
        assert cacheSupportMetrics.getCallCount(methodFindByIdIn) == 4L;
        assert cacheSupportMetrics.getPartialCacheHitCount(methodFindByIdIn) == 3L;
        assert cacheSupportMetrics.getFullCacheHitCount(methodFindByIdIn) == 1L;

        transactionTest.test();

        long id5 = idGen.next(getClass());
        assert userMapper.countById(id5) == 0;
        UserDO userDO5 = new UserDO();
        userDO5.setUsername("username_5");
        userDO5.setEmail("email_5");
        userDO5.setMobile("mobile_5");
        userDO5.setPassword("password_5");
        userDO5.setAge(55);
        userDO5.setCity("hangzhou");
        userDO5.setId(id5);
        assert userMapper.insert(userDO5) > 0;
        assert userMapper.countById(id5) > 0;

        assert userMapper.countByAge(55) == 1;
        UserDO userDO6 = new UserDO();
        userDO6.setUsername("username_6");
        userDO6.setEmail("email_6");
        userDO6.setMobile("mobile_6");
        userDO6.setPassword("password_6");
        userDO6.setAge(55);
        userDO6.setCity("hangzhou");
        userDO6.setId(idGen.next(getClass()));
        assert userMapper.insert(userDO6) > 0;
        assert userMapper.countByAge(55) == 2;

//        System.out.println(cacheAdapterMetrics.getReadCount());
//        System.out.println(cacheAdapterMetrics.getWriteCount());
//        System.out.println(cacheAdapterMetrics.getWriteKeyCount());
    }

}
