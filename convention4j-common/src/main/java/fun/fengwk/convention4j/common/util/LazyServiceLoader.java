package fun.fengwk.convention4j.common.util;

import fun.fengwk.convention4j.common.lang.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * 为了支持加载未定义类的{@link java.util.ServiceLoader}重写版本。
 *
 * @author fengwk
 */
@Slf4j
public class LazyServiceLoader {

    /**
     * @see java.util.ServiceLoader
     */
    private static final String PREFIX = "META-INF/services/";

    /**
     * 遵循{@link java.util.ServiceLoader}的加载规则加载服务。
     *
     * @param serviceClass 服务接口
     * @param <T>          服务接口类型
     * @return 服务列表
     */
    public static <T> List<T> loadServiceIgnoreLoadFailed(Class<T> serviceClass)
        throws ExceptionInInitializerError {
        return loadServiceIgnoreLoadFailed(serviceClass, ClassUtils.getDefaultClassLoader());
    }

    /**
     * 遵循{@link java.util.ServiceLoader}的加载规则加载服务。
     *
     * @param serviceClass 服务接口
     * @param classLoader  类加载器
     * @param <T>          服务接口类型
     * @return 服务列表
     * @throws ExceptionInInitializerError 如果服务初始化失败将抛出该异常
     */
    public static <T> List<T> loadServiceIgnoreLoadFailed(Class<T> serviceClass, ClassLoader classLoader)
        throws ExceptionInInitializerError {
        List<T> services = new ArrayList<>();
        List<LazyServiceLoader.Provider<T>> providers = loadProviders(serviceClass, classLoader);
        for (LazyServiceLoader.Provider<T> provider : providers) {
            try {
                T service = provider.tryLoad();
                if (service != null) {
                    services.add(service);
                } else {
                    log.debug("Load service failed, serviceClass: {}, providerName: {}, classLoader: {}",
                        serviceClass, provider.providerName, classLoader, provider.getError());
                }
            } catch (NoSuchMethodException | InvocationTargetException
                     | InstantiationException | IllegalAccessException ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
        return services;
    }

    /**
     * 遵循{@link java.util.ServiceLoader}的加载规则加载服务提供者。
     *
     * @param serviceClass 服务接口
     * @param <T>          服务接口类型
     * @return 服务提供者列表
     * @throws ExceptionInInitializerError 如果服务初始化失败将抛出该异常
     */
    public static <T> List<Provider<T>> loadProviders(Class<T> serviceClass) {
        return loadProviders(serviceClass, ClassUtils.getDefaultClassLoader());
    }

    /**
     * 遵循{@link java.util.ServiceLoader}的加载规则加载服务提供者。
     *
     * @param serviceClass 服务接口
     * @param classLoader  类加载器
     * @param <T>          服务接口类型
     * @return 服务提供者列表
     */
    public static <T> List<Provider<T>> loadProviders(Class<T> serviceClass, ClassLoader classLoader) {
        List<Provider<T>> providers = new ArrayList<>();
        Set<String> providerNames = loadServiceClassNames(serviceClass, classLoader);
        for (String providerName : providerNames) {
            providers.add(new Provider<>(serviceClass, providerName, classLoader));
        }
        return providers;
    }

    private static <T> Set<String> loadServiceClassNames(Class<T> serviceClass, ClassLoader classLoader) {
        Set<String> providerNames = new HashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(PREFIX + serviceClass.getName());
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    int lc = 1;
                    while ((lc = parseLine(serviceClass, url, reader, lc, providerNames)) >= 0) ;
                }
            }
        } catch (IOException ex) {
            log.error("Load service configs error, serviceClass: {}, classLoader: {}",
                serviceClass, classLoader, ex);
        }
        return providerNames;
    }

    private static int parseLine(Class<?> service, URL u, BufferedReader r, int lc, Set<String> names)
        throws IOException {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                fail(service, u, lc, "Illegal configuration-file syntax");
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            int start = Character.charCount(cp);
            for (int i = start; i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            names.add(ln);
        }
        return lc + 1;
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
        throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }

    private static void fail(Class<?> service, String msg)
        throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    public static class Provider<T> {

        private final Class<T> serviceClass;
        private final String providerName;
        private final ClassLoader classLoader;
        private T provider;
        private Throwable error;

        public Provider(Class<T> serviceClass, String providerName, ClassLoader classLoader) {
            this.serviceClass = serviceClass;
            this.providerName = providerName;
            this.classLoader = classLoader;
        }

        /**
         * 尝试加载服务提供者，如果加载失败将返回null
         *
         * @return 服务提供者，如果提供者类失败将返回null
         * @throws ClassCastException        如果服务提供者定义的类型错误将抛出该异常
         * @throws NoSuchMethodException     如果服务提供者没有定义无参构造函数将抛出该异常
         * @throws InvocationTargetException 如果服务提供者构造函数调用失败将抛出该异常
         * @throws InstantiationException    如果服务提供者无法实例化将抛出该异常
         * @throws IllegalAccessException    如果服务提供者构造函数无法访问将抛出该异常
         */
        public T tryLoad() throws ClassCastException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
            if (provider != null) {
                return provider;
            }
            if (error != null) {
                return null;
            }

            Class<?> providerClass;
            try {
                providerClass = Class.forName(providerName, true, classLoader);
            } catch (Throwable error) {
                this.error = error;
                return null;
            }

            if (!serviceClass.isAssignableFrom(providerClass)) {
                throw new ClassCastException("Provider '" + providerClass
                    + "' not a subtype of service '" + serviceClass + "'");
            }

            Constructor<?> constructor = providerClass.getConstructor();
            this.provider = serviceClass.cast(constructor.newInstance());
            return provider;
        }

        public Throwable getError() {
            return error;
        }

    }

}
