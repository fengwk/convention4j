package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class JsonHandlerTest {

    @Autowired
    public StudentMapper studentMapper;

    @Test
    public void test() {
        Student student = new Student();
        StudentInfo studentInfo = new StudentInfo();
        student.setInfo(studentInfo);
        studentInfo.setName("fwk");
        studentInfo.setAge(12);
        assert studentMapper.insert(student) == 1;
        studentInfo.setAge(13);
        assert studentMapper.updateById(student) == 1;
        assert studentMapper.findAll().size() == 1;
    }

}
