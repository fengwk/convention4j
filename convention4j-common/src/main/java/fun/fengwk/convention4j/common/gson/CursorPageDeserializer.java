package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.common.page.CursorPage;
import fun.fengwk.convention4j.common.page.CursorPageImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class CursorPageDeserializer implements JsonDeserializer<CursorPage<?, ?>> {

    @Override
    public CursorPage<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type cursorPageType) {
        if (cursorPageType instanceof ParameterizedType) {
            ParameterizedType cursorPagePt = (ParameterizedType) cursorPageType;
            return $Gson$Types.newParameterizedTypeWithOwner(null, CursorPageImpl.class,
                    cursorPagePt.getActualTypeArguments());
        } else {
            return CursorPageImpl.class;
        }
    }

}
