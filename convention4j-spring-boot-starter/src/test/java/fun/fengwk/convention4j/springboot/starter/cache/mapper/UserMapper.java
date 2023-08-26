package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheReadMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheWriteMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.IdKey;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.Key;
import org.checkerframework.checker.units.qual.K;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface UserMapper extends LongIdCacheMapper<UserDO> {

    @CacheWriteMethod
    int insert(UserDO record);

    @CacheWriteMethod
    int insertAll(Collection<UserDO> records);

    @CacheWriteMethod
    int deleteById(@IdKey("id") Long id);

    @CacheWriteMethod
    int deleteByIdIn(@IdKey("id") Collection<Long> id);

    @CacheWriteMethod
    int updateByIdSelective(UserDO record);

    @CacheReadMethod
    int countById(@IdKey("id") Long id);

    @CacheReadMethod
    int countByAge(@IdKey("age") Integer age);

    @CacheReadMethod(useIdQuery = true)
    UserDO findById(@IdKey("id") Long id);

    @CacheReadMethod(useIdQuery = true)
    List<UserDO> findByIdIn(@IdKey("id") Collection<Long> ids);

    @CacheReadMethod
    List<UserDO> findByAgeOrderByIdDesc(@Key("age") int age);

    @CacheReadMethod
    List<UserDO> findByAgeAndCity(@Key(value = "age", selective = true) @Param("age") int age,
                                  @Key(value = "city") @Param("city") String city);

}
