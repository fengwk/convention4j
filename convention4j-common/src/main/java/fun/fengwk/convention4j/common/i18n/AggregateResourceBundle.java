package fun.fengwk.convention4j.common.i18n;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * copy from hibernate validator lib.
 *
 * <pre>
 * ResourceBundle.getBundle("message", Locale.CHINA, AggregateResourceBundle.CONTROL);
 * </pre>
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
            return properties.size() == 0 ? null : new AggregateResourceBundle(properties);
        }

        private Properties load(String resourceName, ClassLoader loader) throws IOException {
            Properties aggregatedProperties = new Properties();
            Enumeration<URL> urls = loader.getResources(resourceName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());
                aggregatedProperties.putAll(properties);
            }
            return aggregatedProperties;
        }
    }

}
