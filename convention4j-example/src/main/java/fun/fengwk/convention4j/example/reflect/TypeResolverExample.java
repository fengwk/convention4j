package fun.fengwk.convention4j.example.reflect;

import fun.fengwk.convention4j.common.reflect.TypeResolver;

/**
 * @author fengwk
 */
public class TypeResolverExample {

    static class Parent<T> {}

    static class Children extends Parent<String> {}

    public static void main(String[] args) {
        TypeResolver typeResolver = new TypeResolver(Children.class);
        System.out.println(typeResolver.as(Parent.class).asParameterizedType().getActualTypeArguments()[0]);
    }

}
