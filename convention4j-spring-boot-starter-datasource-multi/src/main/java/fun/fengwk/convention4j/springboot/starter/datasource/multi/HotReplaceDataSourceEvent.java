package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import org.springframework.context.ApplicationEvent;

/**
 * @author fengwk
 */
public class HotReplaceDataSourceEvent extends ApplicationEvent {

    private final String name;

    public HotReplaceDataSourceEvent(String name, DataSourceConfig source) {
        super(source);
        this.name = name;
    }

    public DataSourceConfig getDataSourceConfig() {
        return (DataSourceConfig) getSource();
    }

    public String getName() {
        return name;
    }

}
