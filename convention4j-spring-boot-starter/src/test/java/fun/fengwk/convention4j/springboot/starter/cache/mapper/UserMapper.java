package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.Selective;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.EvictIndex;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.EvictObject;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.ListenKey;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface UserMapper extends CacheableMapper<UserDO, Long> {

    @MapperWriteMethod
    int insert(@EvictObject UserDO record);

    @MapperWriteMethod
    int insertAll(@EvictObject Collection<UserDO> records);

    @MapperWriteMethod
    int deleteById(@EvictIndex Long id);

    @MapperWriteMethod
    int deleteByIdIn(@EvictIndex Collection<Long> id);

    @MapperWriteMethod
    int updateByIdSelective(@EvictIndex("id") UserDO record);

    @MapperReadMethod
    int countById(@ListenKey("id") Long id);

    @MapperReadMethod
    int countByAge(@ListenKey("age") Integer age);

    @MapperReadMethod
    UserDO findById(@ListenKey("id") Long id);

    @MapperReadMethod
    List<UserDO> findByIdIn(@ListenKey("id") Collection<Long> ids);

    @MapperReadMethod
    List<UserDO> findByAgeOrderByIdDesc(@ListenKey("age") int age);

    @MapperReadMethod
    List<UserDO> findByAgeAndCity(@ListenKey(value = "age", required = false)  @Param("age") @Selective Integer age,
                                  @ListenKey(value = "city", required = false)  @Param("city") @Selective String city);

}
