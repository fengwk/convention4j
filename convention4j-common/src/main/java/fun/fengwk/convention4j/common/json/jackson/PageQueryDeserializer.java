package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import fun.fengwk.convention4j.api.page.PageQuery;

import java.io.IOException;

/**
 * @author fengwk
 */
public class PageQueryDeserializer extends GenericsJsonDeserializer<PageQueryDeserializer, PageQuery> {

    @Override
    protected PageQueryDeserializer newInstance() {
        return new PageQueryDeserializer();
    }

    @Override
    public PageQuery deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        JavaType gen0 = generic(0);
        if (gen0 == null) {
            gen0 = ctx.getTypeFactory().constructType(Object.class);
        }
        JavaType javaType = ctx.getTypeFactory().constructParametricType(PageQueryBean.class, gen0);
        PageQueryBean bean = ctx.readValue(p, javaType);
        return new PageQuery(bean.getPageNumber(), bean.getPageSize());
    }

}
