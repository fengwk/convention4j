package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author fengwk
 */
@ConditionalOnClass(HikariDataSource.class)
@Import({MultiDataSourceRegister.class, DataSourcePoolMetadataProvidersConfiguration.class})
// TODO 未添加DataSourceCheckpointRestoreConfiguration，关注下是否会有其它影响
@EnableConfigurationProperties(MultiDataSourceProperties.class)
@AutoConfiguration(before = {SqlInitializationAutoConfiguration.class, TransactionAutoConfiguration.class})
public class MultiDataSourceAutoConfiguration {

}
