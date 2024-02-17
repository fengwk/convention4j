package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import fun.fengwk.convention4j.api.page.CursorPageQuery;

import java.io.IOException;

/**
 * @author fengwk
 */
public class CursorPageQueryDeserializer extends GenericsJsonDeserializer<CursorPageQueryDeserializer, CursorPageQuery> {

    @Override
    protected CursorPageQueryDeserializer newInstance() {
        return new CursorPageQueryDeserializer();
    }

    @Override
    public CursorPageQuery deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        JavaType gen0 = generic(0);
        if (gen0 == null) {
            gen0 = ctx.getTypeFactory().constructType(Object.class);
        }
        JavaType javaType = ctx.getTypeFactory().constructParametricType(CursorPageQueryBean.class, gen0);
        CursorPageQueryBean bean = ctx.readValue(p, javaType);
        return new CursorPageQuery(bean.getCursor(), bean.getLimit());
    }

}
