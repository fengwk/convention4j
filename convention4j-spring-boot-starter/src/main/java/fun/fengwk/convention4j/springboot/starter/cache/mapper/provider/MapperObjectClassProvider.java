package fun.fengwk.convention4j.springboot.starter.cache.mapper.provider;

import fun.fengwk.convention4j.common.cache.exception.CacheInitializationException;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.ObjectClassProvider;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.CacheableMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.lang.reflect.AnnotatedElement;

/**
 * @author fengwk
 */
@Slf4j
public class MapperObjectClassProvider implements ObjectClassProvider {

    @Override
    public Class<?> getObjectClass(AnnotatedElement element) {
        if (!(element instanceof Class) || !CacheableMapper.class.isAssignableFrom((Class<?>) element)) {
            log.error("CacheMapperIndexClassProvider only support CacheableMapper implement class, element: {}",element);
            throw new CacheInitializationException(
                "CacheMapperIndexClassProvider only support CacheableMapper implement class.");
        }
        ResolvableType rt = ResolvableType.forClass((Class<?>) element);
        return rt.as(CacheableMapper.class).getGeneric(0).resolve();
    }

}
