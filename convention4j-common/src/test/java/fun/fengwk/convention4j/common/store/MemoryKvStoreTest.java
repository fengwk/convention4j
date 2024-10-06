package fun.fengwk.convention4j.common.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fengwk
 */
public class MemoryKvStoreTest {

    @Test
    public void test1() throws Exception {
        int limit = 1000000;

        MemoryKvStore<String, Integer> memoryKvStore = new MemoryKvStore<>();

        // t1
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("t1_" + i, i, -1);
            }
        }).start();

        // t2
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("t2_" + i, i, 0);
            }
        }).start();

        // t3
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("t3_" + i, i, 10);
            }
        }).start();

        // t4
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("t4_" + i, i, 100);
            }
        }).start();

        // t5
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("t5_" + i, i, 500);
            }
        }).start();

        Thread.sleep(10000L);

        assertEquals(limit, memoryKvStore.size());
        assertEquals(0, memoryKvStore.heapSize());

        memoryKvStore.close();
    }

    @Test
    public void test2() throws Exception {
        int limit = 1000000;

        MemoryKvStore<String, Integer> memoryKvStore = new MemoryKvStore<>();

        // same tt
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("tt_" + i, i, -1);
            }
        }).start();

        // same tt
        new Thread(() -> {
            for (int i = 0; i < limit; i++) {
                memoryKvStore.put("tt_" + i, i, -1);
            }
        }).start();

        Thread.sleep(10000L);

        assertEquals(limit, memoryKvStore.size());
        assertEquals(0, memoryKvStore.heapSize());

        memoryKvStore.close();
    }

}
