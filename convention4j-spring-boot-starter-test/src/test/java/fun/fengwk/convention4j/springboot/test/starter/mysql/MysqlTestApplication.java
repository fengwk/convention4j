package fun.fengwk.convention4j.springboot.test.starter.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengwk
 */
@MapperScan
@SpringBootApplication
public class MysqlTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MysqlTestApplication.class, args);
    }

}
