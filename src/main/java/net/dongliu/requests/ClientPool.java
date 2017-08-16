package net.dongliu.requests;

import net.dongliu.requests.utils.InputOutputs;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Client pool is for original non-client setting Requests class.
 */
@ThreadSafe
class ClientPool {

    private static final Map<Key, Client> clientMap = new HashMap<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ClientPool() {
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                lock.writeLock().lock();
                try {
                    for (Client client : clientMap.values()) {
                        InputOutputs.closeQuietly(client);
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }));
    }

    private static ClientPool instance = new ClientPool();

    public static ClientPool getInstance() {
        return instance;
    }

    public Client getClient(ClientBuilder builder) {
        Key key = new Key(builder);
        lock.readLock().lock();
        try {
            Client client = clientMap.get(key);
            if (client != null) {
                return client;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            Client client = clientMap.get(key);
            if (client != null) {
                return client;
            }
            client = builder.create();
            clientMap.put(key, client);
            return client;
        } finally {
            lock.writeLock().unlock();
        }
    }

    static class Key {
        private final int socksTimeout;
        private final int connectTimeout;
        @Nonnull
        private final String proxy;
        private final boolean verify;
        @Nonnull
        private final List<CertificateInfo> certs;
        private final boolean followRedirect;
        private final boolean compress;
        private final boolean keepAlive;

        public Key(ClientBuilder builder) {
            this.socksTimeout = builder.socksTimeout;
            this.connectTimeout = builder.connectTimeout;
            this.proxy = String.valueOf(builder.proxy);
            this.verify = builder.verify;
            this.certs = builder.certs;
            this.followRedirect = builder.followRedirect;
            this.compress = builder.compress;
            this.keepAlive = builder.keepAlive;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (socksTimeout != key.socksTimeout) return false;
            if (connectTimeout != key.connectTimeout) return false;
            if (verify != key.verify) return false;
            if (followRedirect != key.followRedirect) return false;
            if (compress != key.compress) return false;
            if (keepAlive != key.keepAlive) return false;
            if (!proxy.equals(key.proxy)) return false;
            return certs.equals(key.certs);
        }

        @Override
        public int hashCode() {
            int result = socksTimeout;
            result = 31 * result + connectTimeout;
            result = 31 * result + proxy.hashCode();
            result = 31 * result + (verify ? 1 : 0);
            result = 31 * result + certs.hashCode();
            result = 31 * result + (followRedirect ? 1 : 0);
            result = 31 * result + (compress ? 1 : 0);
            result = 31 * result + (keepAlive ? 1 : 0);
            return result;
        }
    }
}
