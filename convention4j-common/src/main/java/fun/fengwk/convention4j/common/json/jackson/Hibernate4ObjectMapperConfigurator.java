package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.Module;
import com.google.auto.service.AutoService;

/**
 * @author fengwk
 */
@AutoService(ObjectMapperConfigurator.class)
public class Hibernate4ObjectMapperConfigurator extends BaseModuleObjectMapperConfigurator {

    @Override
    protected String moduleClassName() {
        return "com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module";
    }

    @Override
    protected Module newModule() throws Exception {
        Class.forName("javax.persistence.Transient"); // hibernate依赖，如果没有该依赖就不注入了
        return super.newModule();
    }

}
