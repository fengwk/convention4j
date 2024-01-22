package fun.fengwk.convention4j.springboot.starter.cache.annotation.meta;

import fun.fengwk.convention4j.common.cache.exception.CacheInitializationException;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.*;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.ObjectClassProvider;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

/**
 * @author fengwk
 */
public class CacheAnnotationMetaReader {

    private CacheAnnotationMetaReader() {}

    public static ReadMethodMeta findReadMethodMeta(AnnotatedElement element) {
        MergedAnnotation<ReadMethod> mergedAnnotation = findAnnotation(element, ReadMethod.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        return new ReadMethodMeta(
            mergedAnnotation.getString("name"),
            mergedAnnotation.getString("version"));
    }

    public static WriteMethodMeta findWriteMethodMeta(AnnotatedElement element) {
        MergedAnnotation<WriteMethod> mergedAnnotation = findAnnotation(element, WriteMethod.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        return new WriteMethodMeta(
            mergedAnnotation.getString("objQueryMethod"));
    }

    public static CacheSupportMeta findCacheSupportMeta(AnnotatedElement element) {
        MergedAnnotation<CacheSupport> mergedAnnotation = findAnnotation(element, CacheSupport.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        Class<?> objClass = mergedAnnotation.getClass("objClass");
        if (ObjectClassProvider.class.isAssignableFrom(objClass)) {
            objClass = ((ObjectClassProvider) newInstance(objClass)).getObjectClass(element);
        }
        return new CacheSupportMeta(
            mergedAnnotation.getString("version"),
            mergedAnnotation.getInt("expireSeconds"),
            Objects.requireNonNull(objClass),
            (Class<? extends WriteTransactionSupport>) mergedAnnotation.getClass("writeTransactionSupport"));
    }

    public static EvictObjectMeta findEvictObjectMeta(AnnotatedElement element) {
        MergedAnnotation<EvictObject> mergedAnnotation = findAnnotation(element, EvictObject.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        return new EvictObjectMeta();
    }

    public static EvictIndexMeta findEvictIndexMeta(AnnotatedElement element) {
        MergedAnnotation<EvictIndex> mergedAnnotation = findAnnotation(element, EvictIndex.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        return new EvictIndexMeta(
            mergedAnnotation.getStringArray("value"));
    }

    public static ListenKeyMeta findListenKeyMeta(AnnotatedElement element) {
        MergedAnnotation<ListenKey> mergedAnnotation = findAnnotation(element, ListenKey.class);
        if (!mergedAnnotation.isPresent()) {
            return null;
        }
        return new ListenKeyMeta(
            mergedAnnotation.getString("value"),
            mergedAnnotation.getBoolean("required"));
    }

    private static <T extends Annotation> MergedAnnotation<T> findAnnotation(
        AnnotatedElement element, Class<T> annotationType) {
        return MergedAnnotations
            .from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new CacheInitializationException("Failed to create instance of " + clazz.getName(), ex);
        }
    }

}
