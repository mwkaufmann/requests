package net.dongliu.requests;

import net.dongliu.requests.exception.RequestsException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * @author Liu Dong
 */
class CustomCertTrustManager implements X509TrustManager {

    private final Collection<CertificateInfo> certs;
    private final X509TrustManager sunJSSEX509TrustManager;

    public CustomCertTrustManager(Collection<CertificateInfo> certs) {
        this.certs = certs;
        this.sunJSSEX509TrustManager = load();
    }

    private X509TrustManager load() {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new RequestsException(e);
        }
        for (CertificateInfo cert : certs) {
            try {
                ks.load(new FileInputStream(cert.getPath()), cert.getPassword() == null ? null : cert.getPassword().toCharArray());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (NoSuchAlgorithmException | CertificateException e) {
                throw new RequestsException(e);
            }
        }
        TrustManagerFactory trustManagerFactory;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            trustManagerFactory.init(ks);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException e) {
            throw new RequestsException(e);
        }

        for (TrustManager trustManger : trustManagerFactory.getTrustManagers()) {
            if (trustManger instanceof X509TrustManager) {
                return (X509TrustManager) trustManger;
            }
        }
        throw new RuntimeException("Couldn't initialize X509TrustManager");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return sunJSSEX509TrustManager.getAcceptedIssuers();
    }
}
