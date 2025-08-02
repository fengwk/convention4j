package fun.fengwk.convention4j.spring.cloud.starter.gateway.security;

import fun.fengwk.convention4j.common.lang.StringUtils;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.security.Security;

/**
 * 支持监听证书变更
 * <pre>
 * server:
 *   ssl:
 *     enabled: true
 *     key-store: /etc/letsencrypt/live/kk1.fun/keystore.p12
 *     key-store-password: {password}
 *     key-store-type: PKCS12
 * </pre>
 *
 * @author fengwk
 */
public class SecurityApplicationListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        boolean sslEnabled = environment.getProperty("server.ssl.enabled", Boolean.class, false);
        if (sslEnabled) {
            String keyStore = getSslValue(environment, "keyStore", "key-store");
            if (keyStoreIsFile(keyStore)) {
                String keyStorePassword = getSslValue(environment, "keyStorePassword", "key-store-password");
                String keyStoreType = getSslValue(environment, "keyStoreType", "key-store-type");
                String keyStoreProvider = getSslValue(environment, "keyStoreProvider", "key-store-provider");
                HotReloadX509ExtendedKeyManager.setKeyStore(keyStore);
                HotReloadX509ExtendedKeyManager.setKeyStorePassword(keyStorePassword);
                HotReloadX509ExtendedKeyManager.setKeyStoreType(keyStoreType);
                HotReloadX509ExtendedKeyManager.setKeyStoreProvider(keyStoreProvider);
                Security.insertProviderAt(new HotReloadProvider(), 1);
            }
        }
    }

    private boolean keyStoreIsFile(String keyStore) {
        if (keyStore == null) {
            return false;
        }
        try {
            ResourceUtils.getFile(keyStore);
            return true;
        } catch (FileNotFoundException ignore) {
            return false;
        }
    }

    private String getSslValue(ConfigurableEnvironment environment, String... keys) {
        for (String key : keys) {
            String value = environment.getProperty("server.ssl." + key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

}
