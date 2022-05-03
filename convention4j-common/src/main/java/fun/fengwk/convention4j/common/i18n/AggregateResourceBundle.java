package fun.fengwk.convention4j.common.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 参考hibernate-validator库中实现.
 *
 * <pre>{@code
 * ResourceBundle.getBundle("message", Locale.CHINA, AggregateResourceBundle.CONTROL);
 * }</pre>
 *
 * @author fengwk
 */
public class AggregateResourceBundle extends ResourceBundle {

    public static final Control CONTROL = new AggregateResourceBundleControl();
    private final Properties properties;

    protected AggregateResourceBundle(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected Object handleGetObject(String key) {
        return properties.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> keySet = new HashSet<>(properties.stringPropertyNames());
        if (parent != null) {
            keySet.addAll(Collections.list(parent.getKeys()));
        }
        return Collections.enumeration(keySet);
    }

    private static class AggregateResourceBundleControl extends Control {
        @Override
        public ResourceBundle newBundle(
                String baseName,
                Locale locale,
                String format,
                ClassLoader loader,
                boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            // only *.properties files can be aggregated. Other formats are delegated to the default implementation
            if (!"java.properties".equals(format)) {
                return super.newBundle(baseName, locale, format, loader, reload);
            }

            String resourceName = toBundleName(baseName, locale) + ".properties";
            Properties properties = load(resourceName, loader);
            // 此处修改了获取逻辑，原先的逻辑如果properties为empty就返回null，这样会导致必须要定义语言文件的内容，否则将抛出异常
            // 很可能只定义了语言文件但还未定义其内容，因此此处逻辑改为只要定义了语言文件即可获取到ResourceBundle
            return properties == null ? null : new AggregateResourceBundle(properties);
        }

        private Properties load(String resourceName, ClassLoader loader) throws IOException {
            Enumeration<URL> urls = loader.getResources(resourceName);
            if (!urls.hasMoreElements()) {
                return null;
            }

            Properties aggregatedProperties = new Properties();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                try (InputStream input = url.openStream()) {
                    properties.load(input);
                }
                aggregatedProperties.putAll(properties);
            }
            return aggregatedProperties;
        }

        /**
         * 如果找不到相应语言的文件不采取降级措施，因为使用降级措施可能会导致某些情况下的语义错误，
         * 例如指明要使用英语的情况下如果缺失那么可能系统使用默认没有后缀的语言文件，但却使用了另外的中文语言文件。
         * 这也同时要求使用者要自己定义无后缀的语言文件确保即便不自动降级也能至少使用一类保底的语言文件。
         *
         * @param baseName
         * @param locale
         * @return
         */
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return null;
        }
    }

}
