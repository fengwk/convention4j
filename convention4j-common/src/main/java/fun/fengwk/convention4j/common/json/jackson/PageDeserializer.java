package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import fun.fengwk.convention4j.api.page.DefaultPage;
import fun.fengwk.convention4j.api.page.Page;

import java.io.IOException;

/**
 * @author fengwk
 */
public class PageDeserializer extends GenericsJsonDeserializer<PageDeserializer, Page> {

    @Override
    protected PageDeserializer newInstance() {
        return new PageDeserializer();
    }

    @Override
    public Page deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        JavaType gen0 = generic(0);
        if (gen0 == null) {
            gen0 = ctx.getTypeFactory().constructType(Object.class);
        }
        JavaType javaType = ctx.getTypeFactory().constructParametricType(PageBean.class, gen0);
        PageBean bean = ctx.readValue(p, javaType);
        return new DefaultPage(bean.getPageNumber(), bean.getPageSize(),
            bean.getResults(), bean.getTotalCount());
    }

}
