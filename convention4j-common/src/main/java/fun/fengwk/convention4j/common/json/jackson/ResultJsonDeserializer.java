package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import fun.fengwk.convention4j.api.result.DefaultResult;
import fun.fengwk.convention4j.api.result.Result;

import java.io.IOException;

/**
 * @author fengwk
 */
public class ResultJsonDeserializer extends GenericsJsonDeserializer<ResultJsonDeserializer, Result> {

    @Override
    protected ResultJsonDeserializer newInstance() {
        return new ResultJsonDeserializer();
    }

    @Override
    public Result deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        JavaType gen0 = generic(0);
        if (gen0 == null) {
            gen0 = ctx.getTypeFactory().constructType(Object.class);
        }
        JavaType javaType = ctx.getTypeFactory().constructParametricType(ResultBean.class, gen0);
        ResultBean bean = ctx.readValue(p, javaType);
        return new DefaultResult<>(bean.getStatus(), bean.getCode(), bean.getMessage(), bean.getData(), bean.getErrors());
    }

}
