package fun.fengwk.convention4j.springboot.starter.i18n;

import fun.fengwk.convention4j.common.i18n.StringManager;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;

/**
 * @author fengwk
 */
public class GlobalStringManagerFactory {

    private static volatile StringManagerFactory instance;

    private GlobalStringManagerFactory() {}

    /**
     * 设置全局StringManagerFactory实例。
     *
     * @param stringManagerFactory
     */
    static void setInstance(StringManagerFactory stringManagerFactory) {
        instance = stringManagerFactory;
    }

    /**
     *
     * @return
     * @see StringManagerFactory#getStringManager()
     */
    public static StringManager getStringManager() {
        checkState();
        return instance.getStringManager();
    }

    /**
     *
     * @param keyPrefix
     * @return
     * @see StringManagerFactory#getStringManager(String) 
     */
    public static StringManager getStringManager(String keyPrefix) {
        checkState();
        return instance.getStringManager(keyPrefix);
    }

    /**
     * 
     * @param keyPrefixClass
     * @return
     * @see StringManagerFactory#getStringManager(Class)
     */
    public static StringManager getStringManager(Class<?> keyPrefixClass) {
        checkState();
        return instance.getStringManager(keyPrefixClass);
    }

    /**
     *
     * @param proxyClass
     * @param keyPrefix
     * @param <T>
     * @return
     * @see StringManagerFactory#getStringManagerProxy(Class, String, ClassLoader)
     */
    public static <T> T getStringManagerProxy(Class<T> proxyClass, String keyPrefix, ClassLoader classLoader) {
        checkState();
        return instance.getStringManagerProxy(proxyClass, keyPrefix, classLoader);
    }

    /**
     *
     * @param proxyClass
     * @param keyPrefixClass
     * @param <T>
     * @return
     * @see StringManagerFactory#getStringManagerProxy(Class, Class, ClassLoader)
     */
    public static <T> T getStringManagerProxy(Class<T> proxyClass, Class<?> keyPrefixClass, ClassLoader classLoader) {
        checkState();
        return instance.getStringManagerProxy(proxyClass, keyPrefixClass, classLoader);
    }

    /**
     *
     * @param proxyClass
     * @param <T>
     * @return
     * @see StringManagerFactory#getStringManagerProxy(Class, ClassLoader)
     */
    public static <T> T getStringManagerProxy(Class<T> proxyClass, ClassLoader classLoader) {
        checkState();
        return instance.getStringManagerProxy(proxyClass, classLoader);
    }

    private static void checkState() {
        if (instance == null) {
            throw new IllegalStateException(
                    String.format("%s has not been initialized", GlobalStringManagerFactory.class.getSimpleName()));
        }
    }

}
