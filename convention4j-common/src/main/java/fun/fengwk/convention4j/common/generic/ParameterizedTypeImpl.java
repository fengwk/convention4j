package fun.fengwk.convention4j.common.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author fengwk
 */
class ParameterizedTypeImpl implements ParameterizedType {

    private final Type[] actualTypeArguments;
    private final Type   ownerType;
    private final Type   rawType;

    ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType){
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType;
        this.rawType = rawType;
    }

    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    public Type getOwnerType() {
        return ownerType;
    }

    public Type getRawType() {
        return rawType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(actualTypeArguments, that.actualTypeArguments)
                && Objects.equals(ownerType, that.ownerType)
                && Objects.equals(rawType, that.rawType);
    }

    @Override
    public int hashCode() {
        int result = actualTypeArguments != null ? Arrays.hashCode(actualTypeArguments) : 0;
        result = 31 * result + (ownerType != null ? ownerType.hashCode() : 0);
        result = 31 * result + (rawType != null ? rawType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (ownerType != null) {
            builder.append(typeName(ownerType)).append('.');
        }
        return builder
                .append(typeName(rawType))
                .append('<')
                .append(joinActualTypeArguments())
                .append('>')
                .toString();
    }

    private String joinActualTypeArguments() {
        if (actualTypeArguments == null || actualTypeArguments.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            builder.append(typeName(actualTypeArguments[i]));
            if (i < actualTypeArguments.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private String typeName(Type type) {
        return (type instanceof Class) ? ((Class<?>) type).getName() : type.toString();
    }

}