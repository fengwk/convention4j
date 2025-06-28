package fun.fengwk.convention4j.spring.cloud.starter.gateway.security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * io.netty.handler.ssl.JdkSslServerContext#newSSLContext
 * @author fengwk
 */
abstract class HotReloadKeyManagerFactory extends KeyManagerFactorySpi {

    private final KeyManagerFactory delegate;

    public HotReloadKeyManagerFactory(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.delegate = KeyManagerFactory.getInstance(algorithm, "SunJSSE");
    }

    @Override
    protected void engineInit(KeyStore ks, char[] password) {
        // nothing to do
    }

    @Override
    protected void engineInit(ManagerFactoryParameters spec) {
        // nothing to do
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[] { new HotReloadX509ExtendedKeyManager(delegate) };
    }

    public static class SunX509 extends HotReloadKeyManagerFactory {

        public SunX509() throws NoSuchAlgorithmException, NoSuchProviderException {
            super("SunX509");
        }

    }

    public static class NewSunX509 extends HotReloadKeyManagerFactory {

        public NewSunX509() throws NoSuchAlgorithmException, NoSuchProviderException {
            super("NewSunX509");
        }

    }

}
