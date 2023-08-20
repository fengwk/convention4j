package fun.fengwk.convention4j.springboot.starter.cache.support;

import fun.fengwk.convention4j.common.gson.GsonUtils;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public interface GsonCacheSupport<DATA, ID> extends CacheSupport<DATA, ID> {

    @Override
    default String serializeData(DATA data) {
        return GsonUtils.toJson(data);
    }

    @Override
    default DATA deserializeData(String dataStr, Class<DATA> dataClass) {
        return GsonUtils.fromJson(dataStr, dataClass);
    }

    @Override
    default String serializedReturnValue(Object returnValue) {
        return GsonUtils.toJson(returnValue);
    }

    @Override
    default Object deserializedReturnValue(String returnValueStr, Type returnValueType) {
        return GsonUtils.fromJson(returnValueStr, returnValueType);
    }

}
