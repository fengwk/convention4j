package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class TypeReferenceWrapper<T> extends TypeReference<T> {

    private final Type type;

    public TypeReferenceWrapper(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

}
