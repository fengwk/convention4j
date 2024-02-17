package fun.fengwk.convention4j.common.json.jackson;

import com.google.auto.service.AutoService;

/**
 * @author fengwk
 */
@AutoService(ObjectMapperConfigurator.class)
public class JodaObjectMapperConfigurator extends BaseModuleObjectMapperConfigurator {

    @Override
    protected String moduleClassName() {
        return "com.fasterxml.jackson.datatype.joda.JodaModule";
    }

}
