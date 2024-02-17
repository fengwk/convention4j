package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fun.fengwk.convention4j.api.result.Result;

import java.io.IOException;

/**
 * @author fengwk
 */
public class ResultJsonSerializer extends JsonSerializer<Result> {

    @Override
    public void serialize(Result value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            ResultBean bean = new ResultBean();
            bean.setStatus(value.getStatus());
            bean.setMessage(value.getMessage());
            bean.setData(value.getData());
            bean.setErrors(value.getErrors());
            serializers.findValueSerializer(bean.getClass()).serialize(bean, gen, serializers);
        }
    }

}
