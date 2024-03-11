package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.fengwk.convention4j.common.lang.ClassUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public abstract class BaseModuleObjectMapperConfigurator implements ObjectMapperConfigurator {

    protected abstract String moduleClassName();

    protected Module newModule() throws Exception {
        Class<?> clazz = Class.forName(moduleClassName(), false, ClassUtils.getDefaultClassLoader());
        return (Module) clazz.getConstructor().newInstance();
    }

    @Override
    public void configure(ObjectMapper objectMapper) {
        Module module = null;
        try {
            module = newModule();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("Jackson module '{}' not found", moduleClassName(), ex);
            } else {
                log.debug("Jackson module '{}' not found", moduleClassName());
            }
            /*
            // 忽略缺少依赖的模块
            boolean noClassErr = false;
            Throwable err = ex;
            while (err.getCause() != null && err.getCause() != err) {
                err = err.getCause();
                if (err instanceof ClassNotFoundException || err instanceof NoClassDefFoundError) {
                    noClassErr = true;
                }
            }
            if (noClassErr) {
                log.debug("Module not found", err);
            } else {
                throw ex;
            }
             */
        }
        if (module != null) {
            objectMapper.registerModule(module);
            log.info("Jackson module '{}' loaded", moduleClassName());
        }
    }

}
