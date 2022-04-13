package fun.fengwk.convention4j.springboot.test.starter.mysql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MysqlTestApplication.class)
public class MysqlTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        String username = "xxx";
        UserDO userDO = new UserDO();
        userDO.setId(123L);
        userDO.setGmtCreate(LocalDateTime.now());
        userDO.setGmtModified(LocalDateTime.now());
        userDO.setUsername(username);
        assert userMapper.insert(userDO) == 1;
        assert userMapper.findById(123L).getUsername().equals(username);
        assert userMapper.findAll().size() == 3;
        assert userMapper.findByUsernameStartingWith("fe").get(0).getId() == 1;
    }

}
