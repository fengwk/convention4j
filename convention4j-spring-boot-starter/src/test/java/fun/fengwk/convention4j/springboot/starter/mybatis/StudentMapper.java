package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.automapper.annotation.AutoMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface StudentMapper extends BaseMapper {

    int insert(Student student);

    int updateById(Student student);

    List<Student> findAll();

}
