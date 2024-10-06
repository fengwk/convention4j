package fun.fengwk.convention4j.common.fs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fengwk
 */
@Slf4j
public class FileWatcherManagerTest {

    @Test
    public void test1() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile1 = new File(tmpDir, UUID.randomUUID().toString());
        File tmpFile2 = new File(tmpDir, UUID.randomUUID().toString());

        AtomicInteger createCounter = new AtomicInteger();
        AtomicInteger deleteCounter = new AtomicInteger();
        AtomicInteger modifyCounter = new AtomicInteger();

        FileWatcherListener listener = new FileWatcherListener() {
            @Override
            public void onEntryCreate() {
                createCounter.incrementAndGet();
            }
            @Override
            public void onEntryDelete() {
                deleteCounter.incrementAndGet();
            }
            @Override
            public void onEntryModify() {
                modifyCounter.incrementAndGet();
            }
        };

        FileWatcherManager fileWatcherManager = FileWatcherManager.getInstance();

        CountDownLatch cdl = new CountDownLatch(2);

        new Thread(() -> {
            try {
                fileWatcherManager.watch(tmpFile1.toPath(), listener);
                assertTrue(tmpFile1.createNewFile());
                tmpFile1.deleteOnExit();
                appendText(tmpFile1);
            } catch (IOException e) {
                log.error("Watch temp file1 failed", e);
            }
            cdl.countDown();
        }).start();

        new Thread(() -> {
            try {
                fileWatcherManager.watch(tmpFile2.toPath(), listener);
                assertTrue(tmpFile2.createNewFile());
                appendText(tmpFile2);
                sleep(1500L); // 防止连续两次写入被合并为一次
                appendText(tmpFile2);
                assertTrue(tmpFile2.delete());
            } catch (IOException e) {
                log.error("Watch temp file1 failed", e);
            }
            cdl.countDown();
        }).start();

        new Thread(() -> {
            try {
                cdl.await();
            } catch (InterruptedException ignore) {}
            try {
                fileWatcherManager.close();
            } catch (IOException e) {
                log.error("close file watcher manager failed", e);
            }
        }).start();

        sleep(1500L); // 等待注册启动
        fileWatcherManager.awaitClosed();

        assertEquals(2, createCounter.get());
        assertEquals(1, deleteCounter.get());
        assertEquals(3, modifyCounter.get());
    }

    @Test
    public void test2() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File tmpFile1 = new File(tmpDir, UUID.randomUUID().toString());
        File tmpFile2 = new File(tmpDir, UUID.randomUUID().toString());

        AtomicInteger createCounter = new AtomicInteger();
        AtomicInteger deleteCounter = new AtomicInteger();
        AtomicInteger modifyCounter = new AtomicInteger();

        FileWatcherListener listener = new FileWatcherListener() {
            @Override
            public void onEntryCreate() {
                createCounter.incrementAndGet();
            }
            @Override
            public void onEntryDelete() {
                deleteCounter.incrementAndGet();
            }
            @Override
            public void onEntryModify() {
                modifyCounter.incrementAndGet();
            }
        };

        FileWatcherManager fileWatcherManager = FileWatcherManager.getInstance();

        CountDownLatch cdl = new CountDownLatch(2);

        new Thread(() -> {
            try {
                fileWatcherManager.watch(tmpFile1.toPath(), listener);
                assertTrue(tmpFile1.createNewFile());
                tmpFile1.deleteOnExit();
                appendText(tmpFile1);
                sleep(1500L);
                fileWatcherManager.unwatch(tmpFile1.toPath());
            } catch (IOException e) {
                log.error("Watch temp file1 failed", e);
            }
            cdl.countDown();
        }).start();

        new Thread(() -> {
            try {
                fileWatcherManager.watch(tmpFile2.toPath(), listener);
                assertTrue(tmpFile2.createNewFile());
                appendText(tmpFile2);
                sleep(1500L); // 防止连续写入合并为一次
                appendText(tmpFile2);
                assertTrue(tmpFile2.delete());
                sleep(1500L); // 防止删除无法被感知
                fileWatcherManager.unwatch(tmpFile2.toPath());
            } catch (IOException e) {
                log.error("Watch temp file1 failed", e);
            }
            cdl.countDown();
        }).start();

        sleep(1500L); // 等待注册启动
        fileWatcherManager.awaitClosed();

        assertEquals(2, createCounter.get());
        assertEquals(1, deleteCounter.get());
        assertEquals(3, modifyCounter.get());
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void appendText(File file) {
        String text = UUID.randomUUID().toString();
        try {
            Files.writeString(file.toPath(), text, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            log.error("Append text to file failed", ex);
        }
    }

}
