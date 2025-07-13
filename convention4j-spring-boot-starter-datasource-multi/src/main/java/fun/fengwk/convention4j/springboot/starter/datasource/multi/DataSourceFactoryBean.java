package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.fengwk.convention4j.common.util.NullSafe;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author fengwk
 */
@Setter
public class DataSourceFactoryBean implements FactoryBean<HotReplaceDataSource> {

    private String name;
    private DataSourceConfig config;

    @Override
    public HotReplaceDataSource getObject() {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(config, "config must not be null");
        return new HotReplaceDataSource(name, this::buildDataSource, config);
    }

    @Override
    public Class<?> getObjectType() {
        return HotReplaceDataSource.class;
    }

    private HikariDataSource buildDataSource(DataSourceConfig config) {
        if (config.getDriverClassName() != null) {
            try {
                Class.forName(config.getDriverClassName());
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("datasource driver class name is invalid", ex);
            }
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        for (Map.Entry<String, Object> entry : NullSafe.of(config.getProperties()).entrySet()) {
            hikariConfig.addDataSourceProperty(entry.getKey(), entry.getValue());
        }
        return new HikariDataSource(hikariConfig);
    }

}
