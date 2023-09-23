package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheReadMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheWriteMethod;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.IdKey;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.Key;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface UserMapper extends LongIdCacheMapper<UserPO> {

    @CacheWriteMethod
    int insert(UserPO record);

    @CacheWriteMethod
    int insertAll(Collection<UserPO> records);

    @CacheWriteMethod
    int deleteById(@IdKey("id") Long id);

    @CacheWriteMethod
    int deleteByIdIn(@IdKey("id") Collection<Long> id);

    @CacheWriteMethod
    int updateByIdSelective(UserPO record);

    @CacheReadMethod
    int countById(@IdKey("id") Long id);

    @CacheReadMethod
    int countByAge(@IdKey("age") Integer age);

    @CacheReadMethod(useIdQuery = true)
    UserPO findById(@IdKey("id") Long id);

    @CacheReadMethod(useIdQuery = true)
    List<UserPO> findByIdIn(@IdKey("id") Collection<Long> ids);

    @CacheReadMethod
    List<UserPO> findByAgeOrderByIdDesc(@Key("age") int age);

    @CacheReadMethod
    List<UserPO> findByAgeAndCity(@Key(value = "age", selective = true) @Param("age") int age,
                                  @Key(value = "city") @Param("city") String city);

}
