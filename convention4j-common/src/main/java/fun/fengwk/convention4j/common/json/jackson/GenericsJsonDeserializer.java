package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwk
 */
public abstract class GenericsJsonDeserializer<S extends GenericsJsonDeserializer<S, T>, T> extends JsonDeserializer<T> implements ContextualDeserializer {

    protected volatile List<JavaType> generics;

    void setGenerics(List<JavaType> generics) {
        this.generics = generics;
    }

    protected int genericCount() {
        return generics == null ? 0 : generics.size();
    }

    protected JavaType generic(int index) {
        if (generics != null && index < generics.size()) {
            return generics.get(index);
        }
        return null;
    }

    protected abstract S newInstance();

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        if (property == null) { //  context is generic
            S deserializer = newInstance();
            deserializer.setGenerics(collectGenerics(ctx.getContextualType()));
            return deserializer;
        } else {  //  property is generic
            S deserializer = newInstance();
            deserializer.setGenerics(collectGenerics(property.getType()));
            return deserializer;
        }
    }

    private List<JavaType> collectGenerics(JavaType contextualType) {
        List<JavaType> generics = new ArrayList<>();
        for (int i = 0; i < contextualType.containedTypeCount(); i++) {
            generics.add(contextualType.containedType(i));
        }
        return generics;
    }

}
