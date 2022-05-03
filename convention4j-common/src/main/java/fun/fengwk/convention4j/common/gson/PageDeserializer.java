package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.common.page.Page;
import fun.fengwk.convention4j.common.page.PageImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class PageDeserializer implements JsonDeserializer<Page<?>> {

    @Override
    public Page<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type pageType) {
        if (pageType instanceof ParameterizedType) {
            ParameterizedType pagePt = (ParameterizedType) pageType;
            return $Gson$Types.newParameterizedTypeWithOwner(null, PageImpl.class,
                    pagePt.getActualTypeArguments());
        } else {
            return PageImpl.class;
        }
    }

}
