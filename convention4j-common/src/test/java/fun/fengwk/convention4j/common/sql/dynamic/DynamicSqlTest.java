package fun.fengwk.convention4j.common.sql.dynamic;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author fengwk
 */
public class DynamicSqlTest {

    @Test
    public void test() {
        ExecutableSql executableSql = DynamicSql.parse(
                "  select * from ${namespace}_user_info" +
                "  <where>" +
                "    <foreach item='age' collection='ages' separator='and'>" +
                "      <choose>" +
                "        <when test='age &lt; 3'>" +
                "          user_type=1 and age=#{age}" +
                "        </when>" +
                "        <when test='age >= 10 or age &lt;= 18'>" +
                "          user_type=2 and age=#{age}" +
                "        </when>" +
                "        <otherwise>" +
                "          user_type=3 and age=#{age}" +
                "        </otherwise>" +
                "      </choose>" +
                "    </foreach>" +
                "  </where>" +
                "  <if test='limit != null'>" +
                "    limit #{limit}" +
                "  </if>")
                .addParameter("namespace", "test")
                .addParameter("ages", Arrays.asList(1, 2, 3))
                .addParameter("limit", 10)
                .interpret();

        assert executableSql.getSql().equals("select * from test_user_info where user_type=1 and age=? and user_type=1 and age=? and user_type=2 and age=? limit ?");
        assert Arrays.equals(executableSql.getParameters(), new Object[]{1, 2, 3, 10});
    }

}
