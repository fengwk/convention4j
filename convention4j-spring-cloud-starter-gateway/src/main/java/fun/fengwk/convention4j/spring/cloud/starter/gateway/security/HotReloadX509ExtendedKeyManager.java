package fun.fengwk.convention4j.spring.cloud.starter.gateway.security;

import fun.fengwk.convention4j.common.fs.FileWatcherListener;
import fun.fengwk.convention4j.common.fs.FileWatcherManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author fengwk
 */
@Slf4j
public class HotReloadX509ExtendedKeyManager extends X509ExtendedKeyManager implements FileWatcherListener {

    private static volatile String KEY_STORE = System.getProperty("HotReloadX509ExtendedKeyManager.KEY_STORE");
    private static volatile String KEY_STORE_PASSWORD = System.getProperty("HotReloadX509ExtendedKeyManager.KEY_STORE_PASSWORD");
    private static volatile String KEY_STORE_TYPE = System.getProperty("HotReloadX509ExtendedKeyManager.KEY_STORE_TYPE");
    private static volatile String KEY_STORE_PROVIDER = System.getProperty("HotReloadX509ExtendedKeyManager.KEY_STORE_PROVIDER");
    private final KeyManagerFactory keyManagerFactory;
    private final AtomicReference<X509ExtendedKeyManager> delegateRef = new AtomicReference<>();

    static void setKeyStore(String keyStore) {
        KEY_STORE = keyStore;
    }

    static void setKeyStorePassword(String keyStorePassword) {
        KEY_STORE_PASSWORD = keyStorePassword;
    }

    static void setKeyStoreType(String keyStoreType) {
        KEY_STORE_TYPE = keyStoreType;
    }

    static void setKeyStoreProvider(String keyStoreProvider) {
        KEY_STORE_PROVIDER = keyStoreProvider;
    }

    public HotReloadX509ExtendedKeyManager(KeyManagerFactory keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
        initWatch();
        reloadDelegate();
    }

    private void initWatch() {
        Path source;
        try {
            source = Path.of(ResourceUtils.getURL(KEY_STORE).toURI());
        } catch (URISyntaxException | FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        try {
            FileWatcherManager.getInstance().watch(source, this);
        } catch (IOException ex) {
            throw new IllegalStateException("Can not watch: " + source, ex);
        }
    }

    @Override
    public void onEntryCreate() {
        reloadDelegate();
    }

    @Override
    public void onEntryModify() {
        reloadDelegate();
    }

    private void reloadDelegate() {
        if (delegateRef.getAndSet(getDelegate(keyManagerFactory, getKeyStore())) == null) {
            log.info("Initialized KeyManager, keystoreLocation: {}", KEY_STORE);
        } else {
            log.info("Updated KeyManager, keystoreLocation: {}", KEY_STORE);
        }
    }

    private KeyStore getKeyStore() {
        try {
            return loadStore(KEY_STORE_TYPE, KEY_STORE_PROVIDER, KEY_STORE, KEY_STORE_PASSWORD);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    // org.springframework.boot.web.embedded.netty.SslServerCustomizer#loadStore
    private KeyStore loadStore(String keystoreType, String provider, String keystoreLocation, String password)
        throws Exception {
        keystoreType = (keystoreType != null) ? keystoreType : "JKS";
        char[] passwordChars = (password != null) ? password.toCharArray() : null;
        KeyStore store = (provider != null) ? KeyStore.getInstance(keystoreType, provider)
            : KeyStore.getInstance(keystoreType);
        if (keystoreType.equalsIgnoreCase("PKCS11")) {
            Assert.state(!StringUtils.hasText(keystoreLocation),
                () -> "Keystore location '" + keystoreLocation + "' must be empty or null for PKCS11 key stores");
            store.load(null, passwordChars);
        }
        else {
            try {
                URL url = ResourceUtils.getURL(keystoreLocation);
                try (InputStream stream = url.openStream()) {
                    store.load(stream, passwordChars);
                }
            }
            catch (Exception ex) {
                throw new IllegalStateException("Could not load key store '" + keystoreLocation + "'", ex);
            }
        }
        return store;
    }
    
    private X509ExtendedKeyManager getDelegate(KeyManagerFactory keyManagerFactory, KeyStore keyStore) {
        try {
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD == null ? null : KEY_STORE_PASSWORD.toCharArray());

            for (KeyManager keyManager : this.keyManagerFactory.getKeyManagers()) {
                if (keyManager instanceof X509ExtendedKeyManager) {
                    return (X509ExtendedKeyManager) keyManager;
                }
            }

            throw new IllegalStateException("No X509ExtendedKeyManager available");
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return delegateRef.get().getClientAliases(keyType, issuers);
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return delegateRef.get().chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegateRef.get().getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return delegateRef.get().chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return delegateRef.get().getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return delegateRef.get().getPrivateKey(alias);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return delegateRef.get().chooseEngineClientAlias(keyType, issuers, engine);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        return delegateRef.get().chooseEngineServerAlias(keyType, issuers, engine);
    }

}
