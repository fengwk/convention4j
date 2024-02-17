package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.DefaultCursorPage;

import java.io.IOException;

/**
 * @author fengwk
 */
public class CursorPageDeserializer extends GenericsJsonDeserializer<CursorPageDeserializer, CursorPage> {

    @Override
    protected CursorPageDeserializer newInstance() {
        return new CursorPageDeserializer();
    }

    @Override
    public CursorPage deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        JavaType gen0 = generic(0);
        JavaType gen1 = generic(1);
        if (gen0 == null) {
            gen0 = ctx.getTypeFactory().constructType(Object.class);
        }
        if (gen1 == null) {
            gen1 = ctx.getTypeFactory().constructType(Object.class);
        }
        JavaType javaType = ctx.getTypeFactory().constructParametricType(CursorPageBean.class, gen0, gen1);
        CursorPageBean bean = ctx.readValue(p, javaType);
        return new DefaultCursorPage(bean.getCursor(), bean.getLimit(), bean.getResults(),
            bean.getNextCursor(), bean.isMore());
    }

}
