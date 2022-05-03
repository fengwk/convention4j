package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.common.page.LitePage;
import fun.fengwk.convention4j.common.page.LitePageImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class LitePageDeserializer implements JsonDeserializer<LitePage<?>> {

    @Override
    public LitePage<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type litePageType) {
        if (litePageType instanceof ParameterizedType) {
            ParameterizedType litePagePt = (ParameterizedType) litePageType;
            return $Gson$Types.newParameterizedTypeWithOwner(null, LitePageImpl.class,
                    litePagePt.getActualTypeArguments());
        } else {
            return LitePageImpl.class;
        }
    }

}
