package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.automapper.annotation.TypeHandler;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;
import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class Student {

    @UseGeneratedKeys
    private Long id;
    @TypeHandler(JsonTypeHandler.class)
    private StudentInfo info;
    @TypeHandler(ListStudentInfoTypeHandler.class)
    private List<StudentInfo> infos;

    public static class ListStudentInfoTypeHandler extends JsonTypeHandler<List<StudentInfo>> {}

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("fun.fengwk.convention4j.springboot.starter.mybatis.Student$ListStudentInfoTypeHandler");
        System.out.println(aClass);
    }

}
