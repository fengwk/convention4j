package fun.fengwk.convention4j.springboot.test.starter.mysql;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author fengwk
 */
public class MysqlJdbcTest {

    @Test
    public void test() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "");
        PreparedStatement ps = conn.prepareStatement("create table uu (id bigint)");
        ps.execute();
        conn.close();
    }

}
