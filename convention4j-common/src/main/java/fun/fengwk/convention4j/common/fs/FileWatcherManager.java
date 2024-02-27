package fun.fengwk.convention4j.common.fs;

import fun.fengwk.convention4j.common.concurrent.NamedThreadFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author fengwk
 */
@Slf4j
public class FileWatcherManager implements AutoCloseable {

    private static final ThreadFactory FILE_WATCHER_THREAD_FACTORY = new NamedThreadFactory("FileWatcher");

    private static final FileWatcherManager INSTANCE;

    static {
        INSTANCE = new FileWatcherManager();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                getInstance().close();
            } catch (IOException ex) {
                log.error("Close file watcher manager error", ex);
            }
        }));
    }

    /**
     * parent path -> watcher
     */
    private final ConcurrentMap<Path, Watcher> parentPath2WatcherMap = new ConcurrentHashMap<>();

    private FileWatcherManager() {}

    /**
     * Get singleton instance
     *
     * @return FileWatcherManager
     */
    public static FileWatcherManager getInstance() {
        return INSTANCE;
    }

    /**
     * Watch the path
     *
     * @param path     path
     * @param listener listener
     * @throws IllegalArgumentException can not watch no parent path
     * @throws IOException              watch error
     */
    public void watch(Path path, FileWatcherListener listener) throws IOException {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            throw new IllegalArgumentException("Path has no parent: " + path);
        }

        synchronized (this) {
            Watcher watcher = parentPath2WatcherMap.get(parentPath);
            boolean init = watcher == null;
            if (init) {
                WatchService watchService = parentPath.getFileSystem().newWatchService();
                WatchKey watchKey = parentPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                watcher = new Watcher(watchService, watchKey);
                parentPath2WatcherMap.put(parentPath, watcher);
            }
            watcher.getPath2ListenerMap().put(path.getFileName(), listener);
            if (init) {
                watcher.start();
            }
        }
    }

    /**
     * Unwatch the path
     *
     * @param path path
     * @throws IOException watcher close error
     */
    public void unwatch(Path path) throws IOException {
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return;
        }

        synchronized (this) {
            Watcher watcher = parentPath2WatcherMap.get(parentPath);
            if (watcher != null) {
                watcher.getPath2ListenerMap().remove(path.getFileName());
                if (watcher.getPath2ListenerMap().isEmpty()) {
                    watcher = parentPath2WatcherMap.remove(parentPath);
                    watcher.close();
                }
            }
        }
    }

    public void awaitClosed() {
        for (Watcher watcher : parentPath2WatcherMap.values()) {
            watcher.awaitStopped();
        }
    }

    @Override
    public void close() throws IOException {
        IOException suppressedEx = null;

        synchronized (this) {
            for (Watcher watcher : parentPath2WatcherMap.values()) {
                try {
                    watcher.close();
                } catch (IOException ex) {
                    if (suppressedEx == null) {
                        suppressedEx = new IOException("Close file watcher manager error");
                    }
                    suppressedEx.addSuppressed(ex);
                }
            }

            parentPath2WatcherMap.clear();
        }

        if (suppressedEx != null) {
            throw suppressedEx;
        }
    }

    @Data
    static class Watcher implements Runnable, AutoCloseable {

        /**
         * runner
         */
        final Thread runner = FILE_WATCHER_THREAD_FACTORY.newThread(this);

        /**
         * stop flag
         */
        final CountDownLatch stopFlag = new CountDownLatch(1);

        /**
         * watchService
         */
        final WatchService watchService;

        /**
         * watchKey
         */
        final WatchKey watchKey;

        /**
         * 子路径 -> 监听器
         */
        final ConcurrentMap<Path, FileWatcherListener> path2ListenerMap = new ConcurrentHashMap<>();

        public void start() {
            runner.start();
        }

        @Override
        public void run() {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                WatchKey watched;
                try {
                    watched = watchService.take();
                } catch (InterruptedException ex) {
                    currentThread.interrupt();
                    break;
                } catch (ClosedWatchServiceException ex) {
                    break;
                }

                if (!Objects.equals(watched, watchKey)) {
                    log.warn("Unknown watch key: {}", watched);
                    continue;
                }

                consume(watched.pollEvents());

                if (!watched.reset()) {
                    log.info("Watch key is no longer valid: {}", watched);
                    break;
                }
            }

            stopFlag.countDown();
        }

        @Override
        public void close() throws IOException {
            runner.interrupt();
            awaitStopped();
            watchKey.cancel();
            consume(watchKey.pollEvents());
            watchService.close();
        }

        private void awaitStopped() {
            Thread currentThread = Thread.currentThread();
            boolean interrupted = currentThread.isInterrupted();
            for (; ; ) {
                try {
                    stopFlag.await();
                    break;
                } catch (InterruptedException ignore) {
                    interrupted = true;
                }
            }
            if (interrupted && !currentThread.isInterrupted()) {
                currentThread.interrupt();
            }
        }

        private void consume(List<WatchEvent<?>> watchEvents) {
            for (WatchEvent<?> watchEvent : watchEvents) {
                Path changedPath = (Path) watchEvent.context();
                if (changedPath == null) {
                    continue;
                }

                FileWatcherListener listener = path2ListenerMap.get(changedPath);
                if (listener == null) {
                    continue;
                }

                WatchEvent.Kind<?> kind = watchEvent.kind();
                try {
                    if (Objects.equals(kind, ENTRY_CREATE)) {
                        listener.onEntryCreate();
                    } else if (Objects.equals(kind, ENTRY_MODIFY)) {
                        listener.onEntryModify();
                    } else if (Objects.equals(kind, ENTRY_DELETE)) {
                        listener.onEntryDelete();
                    }
                } catch (Throwable err) {
                    log.error("Uncaught error", err);
                }
            }
        }

    }

}