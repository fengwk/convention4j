package fun.fengwk.convention4j.common.i18n;

import org.junit.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author fengwk
 */
public class StringManagerFactoryTest {

    @Test
    public void testGetStringManager1() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.CHINA, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManager stringManager = stringManagerFactory.getStringManager(StringManagerFactoryTest.class);

        Map<String, Object> ctx = new ConcurrentHashMap<>();
        ctx.put("name", "冯先生");

        String str = stringManager.getString("message", ctx);
        assert str.equals("你好，冯先生");
    }

    @Test
    public void testGetStringManager2() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.ENGLISH, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManager stringManager = stringManagerFactory.getStringManager(StringManagerFactoryTest.class);

        Map<String, Object> ctx = new ConcurrentHashMap<>();
        ctx.put("name", "fengwk");
        ctx.put("age", 18);

        assert stringManager.getString("message2", ctx).equals("fengwk is 18 years old");
    }

    @Test
    public void testGetStringManager3() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.CHINA, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManager stringManager = stringManagerFactory.getStringManager(StringManagerFactoryTest.class);

        String str = stringManager.getString("message");
        assert str.equals("你好，null");
    }
    
    @Test
    public void testGetStringManagerProxy1() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.CHINA, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManagerProxy proxy = stringManagerFactory.getStringManagerProxy(StringManagerProxy.class, StringManagerFactoryTest.class, StringManagerFactoryTest.class.getClassLoader());
        assert proxy.message("冯先生").equals("你好，冯先生");
    }
    
    @Test
    public void testGetStringManagerProxy2() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.ENGLISH, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManagerProxy proxy = stringManagerFactory.getStringManagerProxy(StringManagerProxy.class, StringManagerFactoryTest.class, StringManagerFactoryTest.class.getClassLoader());
        assert proxy.message2("fengwk", 18).equals("fengwk is 18 years old");
    }
    
    interface StringManagerProxy {
        
        String message(@Name("name") String name);
        
        String message2(@Name("name") String name, @Name("age") int age);
        
    }
    
}
