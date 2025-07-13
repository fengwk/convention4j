package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * 多数据源与{@link DataSourceAutoConfiguration}和{@link MybatisAutoConfiguration}冲突，需要通过当前过滤器排除掉
 *
 * @author fengwk
 */
public class MultiDataSourceExcludeImportFilter implements AutoConfigurationImportFilter {

    private static final Set<String> EXCLUDE_AUTO_CONFIGURATION_CLASSES;

    static {
        Set<String> excludeAutoConfigurationClasses = new HashSet<>();
        try {
            // 多数据源与原始的自动配置会产生冲突
            excludeAutoConfigurationClasses.add(DataSourceAutoConfiguration.class.getName());
        } catch (Throwable ignore) {}
        try {
            // 为多数据源创建不同的DataSourceTransaction以支持多数据源事务
            excludeAutoConfigurationClasses.add(DataSourceTransactionManagerAutoConfiguration.class.getName());
        } catch (Throwable ignore) {}
        try {
            excludeAutoConfigurationClasses.add(MybatisAutoConfiguration.class.getName());
        } catch (Throwable ignore) {}
        EXCLUDE_AUTO_CONFIGURATION_CLASSES = excludeAutoConfigurationClasses;
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] result = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            if (EXCLUDE_AUTO_CONFIGURATION_CLASSES.contains(autoConfigurationClasses[i])) {
                result[i] = false;
            } else {
                result[i] = true;
            }
        }
        return result;
    }

}
