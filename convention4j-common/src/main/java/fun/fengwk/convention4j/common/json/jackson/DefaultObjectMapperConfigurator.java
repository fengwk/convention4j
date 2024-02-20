package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.CursorPageQuery;
import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;

/**
 * @author fengwk
 */
@AutoService(ObjectMapperConfigurator.class)
public class DefaultObjectMapperConfigurator implements ObjectMapperConfigurator {

    @Override
    public void configure(ObjectMapper objectMapper) {
        // 反序列化时如果遇到未知属性则忽略而非报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时忽略null值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule defaultModule = new SimpleModule();
        defaultModule.addSerializer(Long.class, new LongJsonSerializer());
        defaultModule.addSerializer(long.class, new LongJsonSerializer());
        defaultModule.addSerializer(Result.class, new ResultJsonSerializer());
        defaultModule.addDeserializer(Result.class, new ResultJsonDeserializer());
        defaultModule.addDeserializer(Page.class, new PageDeserializer());
        defaultModule.addDeserializer(CursorPage.class, new CursorPageDeserializer());
        defaultModule.addDeserializer(PageQuery.class, new PageQueryDeserializer());
        defaultModule.addDeserializer(CursorPageQuery.class, new CursorPageQueryDeserializer());
        objectMapper.registerModule(defaultModule);
    }

}
