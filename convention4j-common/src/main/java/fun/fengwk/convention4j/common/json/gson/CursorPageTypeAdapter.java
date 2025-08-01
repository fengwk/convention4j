package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.internal.GsonTypes;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.DefaultCursorPage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class CursorPageTypeAdapter implements GsonTypeAdapter<CursorPage<?, ?>> {

    @Override
    public JsonElement serialize(CursorPage<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, DefaultCursorPage.class);
    }

    @Override
    public CursorPage<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type cursorPageType) {
        if (cursorPageType instanceof ParameterizedType) {
            ParameterizedType cursorPagePt = (ParameterizedType) cursorPageType;
            return GsonTypes.newParameterizedTypeWithOwner(null, DefaultCursorPage.class,
                    cursorPagePt.getActualTypeArguments());
        } else {
            return DefaultCursorPage.class;
        }
    }

}
