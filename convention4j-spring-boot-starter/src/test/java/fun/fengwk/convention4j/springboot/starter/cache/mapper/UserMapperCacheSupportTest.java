package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
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
public class UserMapperCacheSupportTest {

    @Autowired
    private NamespaceIdGenerator<Long> idGen;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TransactionTest transactionTest;
    @Autowired
    private CacheManagerMetrics cacheManagerMetrics;
//    @Autowired
//    private CacheAdapterMetrics cacheAdapterMetrics;

    @Test
    public void test() throws NoSuchMethodException {
        String methodInsert = UserMapper.class.getMethod("insert", UserDO.class).toString();
        String methodInertAll = UserMapper.class.getMethod("insertAll", Collection.class).toString();
        String methodUpdateByIdSelective = UserMapper.class.getMethod("updateByIdSelective", UserDO.class).toString();
        String methodFindById = UserMapper.class.getMethod("findById", Long.class).toString();
        String methodFindByAgeOrderByIdDesc = UserMapper.class.getMethod("findByAgeOrderByIdDesc", int.class).toString();
        String methodFindByIdIn = UserMapper.class.getMethod("findByIdIn", Collection.class).toString();
        String methodDeleteById = UserMapper.class.getMethod("deleteById", Long.class).toString();
        String methodDeleteByIdIn = UserMapper.class.getMethod("deleteByIdIn", Collection.class).toString();

        UserDO userPO1 = new UserDO();
        userPO1.setUsername("username");
        userPO1.setEmail("email");
        userPO1.setMobile("mobile");
        userPO1.setPassword("password");
        userPO1.setAge(18);
        userPO1.setCity("hangzhou");
        userPO1.setId(idGen.next(getClass()));
        assert userMapper.insert(userPO1) > 0;

        UserDO found = userMapper.findById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodFindById) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodFindById) == 0L;
        assert Objects.equals(userPO1, found);

        UserDO updatePO = new UserDO();
        updatePO.setId(userPO1.getId());
        updatePO.setPassword("password_update");
        assert userMapper.updateByIdSelective(updatePO) > 0;

        found = userMapper.findById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodFindById) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodFindById) == 0L;
        assert Objects.equals(updatePO.getPassword(), found.getPassword());

        userMapper.findById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodFindById) == 3L;
        assert cacheManagerMetrics.getReadHitCount(methodFindById) == 1L;

        UserDO userPO2 = new UserDO();
        userPO2.setUsername("username_2");
        userPO2.setEmail("email_2");
        userPO2.setMobile("mobile_2");
        userPO2.setPassword("password_2");
        userPO2.setAge(18);
        userPO2.setCity("hangzhou");
        userPO2.setId(idGen.next(getClass()));
        assert userMapper.insert(userPO2) > 0;

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
        assert userMapper.insertAll(user34List) > 0;

        userMapper.findById(userPO1.getId());
        assert cacheManagerMetrics.getReadCount(methodFindById) == 4L;
        assert cacheManagerMetrics.getReadHitCount(methodFindById) == 2L;

        assert userMapper.findByAgeOrderByIdDesc(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodFindByAgeOrderByIdDesc) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByAgeOrderByIdDesc) == 0L;
        assert userMapper.findByAgeOrderByIdDesc(18).size() == 2;
        assert cacheManagerMetrics.getReadCount(methodFindByAgeOrderByIdDesc) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByAgeOrderByIdDesc) == 1L;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheManagerMetrics.getReadCount(methodFindByIdIn) == 1L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByIdIn) == 0L;
        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 4;
        assert cacheManagerMetrics.getReadCount(methodFindByIdIn) == 2L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteById(userPO1.getId()) > 0;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 3;
        assert cacheManagerMetrics.getReadCount(methodFindByIdIn) == 3L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByIdIn) == 1L;

        assert userMapper.deleteByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO4.getId())) > 0;

        assert userMapper.findByIdIn(Arrays.asList(userPO1.getId(), userPO2.getId(), userPO3.getId(), userPO4.getId())).size() == 1;
        assert cacheManagerMetrics.getReadCount(methodFindByIdIn) == 4L;
        assert cacheManagerMetrics.getReadHitCount(methodFindByIdIn) == 1L;

        transactionTest.test();

        long id5 = idGen.next(getClass());
        assert userMapper.countById(id5) == 0;
        UserDO userPO5 = new UserDO();
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
        UserDO userPO6 = new UserDO();
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
