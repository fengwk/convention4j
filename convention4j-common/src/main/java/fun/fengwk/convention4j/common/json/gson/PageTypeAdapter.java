package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.internal.GsonTypes;
import fun.fengwk.convention4j.api.page.DefaultPage;
import fun.fengwk.convention4j.api.page.Page;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class PageTypeAdapter implements GsonTypeAdapter<Page<?>> {

    @Override
    public JsonElement serialize(Page<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, DefaultPage.class);
    }

    @Override
    public Page<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type pageType) {
        if (pageType instanceof ParameterizedType) {
            ParameterizedType pagePt = (ParameterizedType) pageType;
            return GsonTypes.newParameterizedTypeWithOwner(null, DefaultPage.class,
                    pagePt.getActualTypeArguments());
        } else {
            return DefaultPage.class;
        }
    }

}
