package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.automapper.annotation.TypeHandler;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class Student {

    @UseGeneratedKeys
    private Long id;
    @TypeHandler(JsonHandler.class)
    private StudentInfo info;

}
