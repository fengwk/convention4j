package fun.fengwk.convention.springboot.starter.i18n;

import fun.fengwk.commons.i18n.StringManager;
import fun.fengwk.commons.i18n.StringManagerFactory;
import fun.fengwk.convention.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class I18nAutoConfigurationTest {

    @Autowired
    private StringManagerFactory stringManagerFactory;
    
    @Test
    public void test1() {
        StringManager stringManager = stringManagerFactory.getStringManager(I18nAutoConfigurationTest.class);
        assert stringManager.getString("message").equals("你好");
    }

    @Test
    public void test2() {
        StringManager stringManager = GlobalStringManagerFactory.getStringManager(I18nAutoConfigurationTest.class);
        assert stringManager.getString("message").equals("你好");
    }
    
}
