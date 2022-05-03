package fun.fengwk.convention4j.common.sql.dynamic;

import fun.fengwk.convention4j.common.MapUtils;
import ognl.Ognl;
import ognl.OgnlException;
import org.junit.Test;

import java.util.Map;

/**
 * @author fengwk
 */
public class OgnlTest {

    static class User {

        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    @Test
    public void test1() throws OgnlException {
        Map<String, User> ctx = MapUtils.newMap("user", new User());
        Object user = Ognl.getValue("user", ctx);
        assert user.equals(ctx.get("user"));
    }

    @Test
    public void test2() throws OgnlException {
        User u = new User();
        u.setUsername("fengwk");
        Map<String, User> ctx = MapUtils.newMap("user", u);
        Object username = Ognl.getValue("user.username", ctx);
        assert username.equals("fengwk");
    }

    @Test
    public void test3() throws OgnlException {
        Map<String, User> ctx = MapUtils.newMap("user", new User());
        assert (boolean) Ognl.getValue("user.username == null", ctx);
    }

    @Test
    public void test4() throws OgnlException {
        Map<String, User> ctx = MapUtils.newMap("user", new User());
        assert (boolean) Ognl.getValue("user.username != null or 1 == 1", ctx);
    }

}
