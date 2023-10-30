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
public interface UserMapper extends CacheableMapper<UserPO, Long> {

    @MapperWriteMethod
    int insert(@EvictObject UserPO record);

    @MapperWriteMethod
    int insertAll(@EvictObject Collection<UserPO> records);

    @MapperWriteMethod
    int deleteById(@EvictIndex Long id);

    @MapperWriteMethod
    int deleteByIdIn(@EvictIndex Collection<Long> id);

    @MapperWriteMethod
    int updateByIdSelective(@EvictIndex("id") UserPO record);

    @MapperReadMethod
    int countById(@ListenKey("id") Long id);

    @MapperReadMethod
    int countByAge(@ListenKey("age") Integer age);

    @MapperReadMethod
    UserPO findById(@ListenKey("id") Long id);

    @MapperReadMethod
    List<UserPO> findByIdIn(@ListenKey("id") Collection<Long> ids);

    @MapperReadMethod
    List<UserPO> findByAgeOrderByIdDesc(@ListenKey("age") int age);

    @MapperReadMethod
    List<UserPO> findByAgeAndCity(@ListenKey(value = "age", required = false)  @Param("age") @Selective Integer age,
                                  @ListenKey(value = "city", required = false)  @Param("city") @Selective String city);

}
