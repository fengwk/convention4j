package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class DataSourceBeanConfig extends DataSourceConfig {

    /**
     * 是否为主数据源
     */
    private Boolean primary;

}
