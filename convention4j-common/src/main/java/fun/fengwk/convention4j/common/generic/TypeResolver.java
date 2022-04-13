package fun.fengwk.convention4j.common.generic;

import java.lang.reflect.*;
import java.util.*;

/**
 * 类型解析器。
 * 
 * @author fengwk
 */
public class TypeResolver {
    
    /**
     * 当前解析器处理的类型。
     */
    private final Type type;
    
    /**
     * 由type解析出的class。
     */
    private final Class<?> resolvedClass;
    
    /**
     * 构造类型解析器。
     * 
     * @param type
     */
    public TypeResolver(Type type) {
        this.type = type;
        this.resolvedClass = resolveClass(type);
    }
    
    /**
     * 
     * @param type
     * @param resolvedClass
     */
    private TypeResolver(Type type, Class<?> resolvedClass) {
        this.type = type;
        this.resolvedClass = resolvedClass;
    }
    
    /**
     * 检查当前解析器对应的类型是否为Class。
     * 
     * @return
     */
    public boolean isClass() {
        return type instanceof Class;
    }
    
    /**
     * 检查当前解析器对应的类型是否为ParameterizedType。
     * 
     * @return
     */
    public boolean isParameterizedType() {
        return type instanceof ParameterizedType;
    }
    
    /**
     * 检查当前解析器对应的类型是否为GenericArrayType。
     * 
     * @return
     */
    public boolean isGenericArrayType() {
        return type instanceof GenericArrayType;
    }
    
    /**
     * 检查当前解析器对应的类型是否为TypeVariable。
     * 
     * @return
     */
    public boolean isTypeVariable() {
        return type instanceof TypeVariable;
    }
    
    /**
     * 检查当前解析器对应的类型是否为WildcardType。
     * 
     * @return
     */
    public boolean isWildcardType() {
        return type instanceof WildcardType;
    }
    
    /**
     * 获取当前解析器对应的类型的Class。
     * 
     * @param <T>
     * @return
     * @throws IllegalStateException 如果当前类型不是Class则抛出该异常。
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> asClass() {
        if (!isClass()) {
            throw new IllegalStateException("Type is not a Class");
        }
        
        return (Class<T>) type;
    }
    
    /**
     * 获取当前解析器对应的类型的ParameterizedType。
     * 
     * @return
     * @throws IllegalStateException 如果当前类型不是ParameterizedType则抛出该异常。
     */
    public ParameterizedType asParameterizedType() {
        if (!isParameterizedType()) {
            throw new IllegalStateException("Type is not a ParameterizedType");
        }
        
        return (ParameterizedType) type;
    }
    
    /**
     * 获取当前解析器对应的类型的GenericArrayType。
     * 
     * @return
     * @throws IllegalStateException 如果当前类型不是GenericArrayType则抛出该异常。
     */
    public GenericArrayType asGenericArrayType() {
        if (!isGenericArrayType()) {
            throw new IllegalStateException("Type is not a GenericArrayType");
        }
        
        return (GenericArrayType) type;
    }
    
    /**
     * 获取当前解析器对应的类型的TypeVariable。
     * 
     * @param <D>
     * @return
     * @throws IllegalStateException 如果当前类型不是TypeVariable则抛出该异常。
     */
    @SuppressWarnings("unchecked")
    public <D extends GenericDeclaration> TypeVariable<D> asTypeVariable() {
        if (!isTypeVariable()) {
            throw new IllegalStateException("Type is not a TypeVariable");
        }
        
        return (TypeVariable<D>) type;
    }
    
    /**
     * 获取当前解析器对应的类型的WildcardType。
     * 
     * @return
     * @throws IllegalStateException 如果当前类型不是WildcardType则抛出该异常。
     */
    public WildcardType asWildcardType() {
        if (!isWildcardType()) {
            throw new IllegalStateException("Type is not a WildcardType");
        }
        
        return (WildcardType) type;
    }
    
    /**
     * 将当前类型解析器转换为目标类型解析器，目标类型应该是当前类型的祖先。
     * 
     * @param target
     * @return
     * @throws IllegalArgumentException 若target并非祖先则抛出该异常。
     */
    public TypeResolver as(Class<?> target) {
        LinkedList<Class<?>> path = new LinkedList<>();
        if (!doFindPath(target, resolvedClass, path)) {
            throw new IllegalArgumentException(
                    String.format("%s is not the ancestor of %s", 
                            target.getSimpleName(), resolvedClass.getSimpleName()));
        }
        
        TypeResolver tr = this;
        path.pop();
        while (!path.isEmpty()) {
            tr = tr.up(path.pop());
        }
        return tr;
    }
    
    private Class<?> resolveClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        
        else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        
        else if (type instanceof GenericArrayType) {
            Class<?> componentClass = resolveClass(((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(componentClass, 0).getClass();
        }
        
        else if (type instanceof TypeVariable) {
            Type bound = resolveBounds(((TypeVariable<?>) type).getBounds());
            return bound == null ? Object.class : resolveClass(bound);
        }
        
        else if (type instanceof WildcardType) {
            Type upperBound = resolveBounds(((WildcardType) type).getUpperBounds());
            if (upperBound != null) {
                return resolveClass(upperBound);
            }
            
            Type lowerBound = resolveBounds(((WildcardType) type).getLowerBounds());
            return lowerBound == null ? Object.class : resolveClass(lowerBound);
        }
        
        else {
            throw new IllegalStateException(String.format("Unsupported type '%s'", type));
        }
    }
    
    private Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        // 默认使用第一个bound
        return bounds[0];
    }
    
    // 从current出发找到target的路径，如果找到该路径返回true，并将路径逆序装在path中
    private boolean doFindPath(Class<?> target, Class<?> current, LinkedList<Class<?>> path) {
        if (current.equals(target)) {
            path.push(current);
            return true;
        }
        
        Type supType = current.getGenericSuperclass();
        if (supType != null) {
            Class<?> supClass = (Class<?>) ((supType instanceof ParameterizedType) ? ((ParameterizedType) supType).getRawType() : supType);
            if (doFindPath(target, supClass, path)) {
                path.push(current);
                return true;
            }
        }
        
        Type[] interfaces = current.getGenericInterfaces();
        if (interfaces != null) {
            for (Type inter : interfaces) {
                Class<?> interClass = (Class<?>) ((inter instanceof ParameterizedType) ? ((ParameterizedType) inter).getRawType() : inter);
                if (doFindPath(target, interClass, path)) {
                    path.push(current);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private TypeResolver up(Class<?> targetClass) {
        Map<TypeVariable<?>, Type> typeVarMap = getTypeVarMap(resolvedClass, type);
        
        Type supType = resolvedClass.getGenericSuperclass();
        if (supType != null && resolveClass(supType) == targetClass) {
            Class<?> supClass = resolveClass(supType);
            if (supClass == targetClass) {
                return new TypeResolver(resolveType(supType, typeVarMap), supClass);
            }
        }
        
        Type[] interfaces = resolvedClass.getGenericInterfaces();
        if (interfaces != null) {
            for (Type interType : interfaces) {
                Class<?> interClass = resolveClass(interType);
                if (interClass == targetClass) {
                    return new TypeResolver(resolveType(interType, typeVarMap), interClass);
                }
            }
        }
        
        throw new AssertionError(
                String.format("%s is not %s's parent class or interface", 
                        targetClass.getSimpleName(), resolvedClass.getSimpleName()));
    }
    
    private Map<TypeVariable<?>, Type> getTypeVarMap(Class<?> clazz, Type type) {
        if (!(type instanceof ParameterizedType)) {
            return Collections.emptyMap();
        }
        
        
        TypeVariable<?>[]  typeVars = clazz.getTypeParameters();
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        
        // TODO for test
        assert typeVars.length == actualTypeArguments.length;
        
        Map<TypeVariable<?>, Type> typeVarMap = new HashMap<>();
        for (int i = 0; i < typeVars.length; i++) {
            typeVarMap.put(typeVars[i], actualTypeArguments[i]);
        }
        return typeVarMap;
    }
    
    private Type resolveType(Type type, Map<TypeVariable<?>, Type> typeVarMap) {
        if (type instanceof Class) {
            return type;
        } 
        
        else if (type instanceof TypeVariable) {
            Type typeVar = typeVarMap.get(type);
            
            // TODO for test
            assert typeVar != null;
            
            return typeVar;
        } 
        
        else if (type instanceof WildcardType) {
            return type;
        } 
        
        else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] oldTypes = parameterizedType.getActualTypeArguments();
            Type[] newTypes = new Type[oldTypes.length];
            for (int i = 0; i < oldTypes.length; i++) {
                newTypes[i] = resolveType(oldTypes[i], typeVarMap);
            }
            
            if (Arrays.equals(oldTypes, newTypes)) {
                return type;
            } else {
                return new ParameterizedTypeImpl(newTypes, parameterizedType.getOwnerType(), parameterizedType.getRawType());
            }
        } 
        
        else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type oldComponentType = genericArrayType.getGenericComponentType();
            Type newComponentType = resolveType(oldComponentType, typeVarMap);
            
            if (Objects.equals(oldComponentType, oldComponentType)) {
                return type;
            } else {
                return new GenericArrayTypeImpl(newComponentType);
            }
        } 
        
        else {
            throw new IllegalStateException(String.format("Unsupported type %s", type.getTypeName()));
        }
    }
    
}
