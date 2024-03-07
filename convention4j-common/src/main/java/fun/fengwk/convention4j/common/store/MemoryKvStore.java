package fun.fengwk.convention4j.common.store;

import fun.fengwk.convention4j.common.concurrent.NamedThreadFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Slf4j
public class MemoryKvStore<K, V> implements Runnable, AutoCloseable {

    /**
     * 清理线程工厂
     */
    private static final NamedThreadFactory THREAD_FACTORY = new NamedThreadFactory("MemoryKvStore-ClearThread");

    /**
     * 初始化堆容量
     */
    private static final int INIT_HEAP_CAPACITY = 8;

    /**
     * 清理线程
     */
    private volatile Thread clearThread = THREAD_FACTORY.newThread(this);

    /**
     * lock
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * heap非空条件
     */
    private final Condition heapNotEmpty = lock.newCondition();

    /**
     * 节点过期或发生了节点变更
     */
    private final Condition nodeExpiredOrChanged = lock.newCondition();

    /**
     * 过期堆，小顶堆
     */
    private volatile Node<K, V>[] heap = new Node[INIT_HEAP_CAPACITY];

    /**
     * 过期堆size
     */
    private volatile int heapSize = 0;

    /**
     * key-value存储
     */
    private final ConcurrentMap<K, Node<K, V>> storeMap = new ConcurrentHashMap<>();

    public MemoryKvStore() {
        clearThread.start();
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        outLoop:
        while (clearThread != null && !currentThread.isInterrupted()) {
            // 获取lock
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                currentThread.interrupt();
                break;
            }

            try {

                // 如果堆中没有元素了则等待notEmpty条件达成
                while (heapSize == 0) {
                    try {
                        log.debug("Wait heap not empty");
                        heapNotEmpty.await();
                    } catch (InterruptedException e) {
                        currentThread.interrupt();
                        break outLoop;
                    }
                }

                // 获取首个堆中元素，如果已经过期进行移除，否则等待过期时间到来
                Node<K, V> node = heap[0];
                long waitMs = node.expireTime - System.currentTimeMillis();
                if (waitMs <= 0) {
                    storeMap.remove(node.key);
                    removeFromHeap(node);
                    log.debug("MemoryKvStore expired node.key: {}, heapSize: {}, size: {}",
                        node.key, heapSize, storeMap.size());
                } else {
                    try {
                        log.debug("Wait node expired or changed, waitMs: {}ms", waitMs);
                        nodeExpiredOrChanged.await(waitMs, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        currentThread.interrupt();
                        break;
                    }
                }

            } finally {
                // 释放lock
                lock.unlock();
            }
        }
    }

    /**
     * 存储指定key的value
     *
     * @param key      key
     * @param value    value
     * @param expireMs 过期时间，单位毫秒，如果设置为-1则表示永不过期
     */
    public void put(K key, V value, long expireMs) {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        // 超时时间为0则不存储直接返回
        if (expireMs == 0) {
            return;
        }

        // 超时时间小于0则表示永不过期使用-1占位，否则计算实际过期时间
        long expireTime = expireMs < 0 ? -1 : System.currentTimeMillis() + expireMs;
        Node<K, V> node = new Node<>(key, value, expireTime);

        lock.lock();
        try {
            Node<K, V> oldNode = storeMap.put(key, node);
            // 如果节点需要过期则加入堆中
            if (expireMs > 0) {
                addToHeap(node);
            }
            // 如果旧节点存在，并且在堆中，则从堆中移除
            if (oldNode != null && oldNode.index != -1) {
                removeFromHeap(oldNode);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除指定key的value
     *
     * @param key key
     * @return value
     */
    public V remove(K key) {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        Node<K, V> node;
        lock.lock();
        try {
            node = storeMap.remove(key);
            // 如果节点存在，并且在堆中，则从堆中移除
            if (node != null && node.index != -1) {
                removeFromHeap(node);
            }
        } finally {
            lock.unlock();
        }
        return node == null ? null : node.value;
    }

    /**
     * 获取指定key的value
     *
     * @param key key
     * @return value
     */
    public V get(K key) {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        Node<K, V> node = storeMap.get(key);
        return node == null ? null : node.value;
    }

    /**
     * 检查当前存储是否包含指定key
     *
     * @param key key
     * @return 是否包含
     */
    public boolean containsKey(K key) {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        return storeMap.containsKey(key);
    }

    /**
     * 获取当前存储快照
     *
     * @return 快照
     */
    public Map<K, V> dump() {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        return storeMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().value));
    }

    /**
     * 获取当前存储大小
     *
     * @return 存储大小
     */
    public int size() {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        return storeMap.size();
    }

    /**
     * for test
     *
     * @return heapSize
     */
    int heapSize() {
        if (clearThread == null) {
            throw new IllegalStateException("MemoryKvStore has been closed");
        }

        return heapSize;
    }

    private void addToHeap(Node<K, V> node) {
        // 如果堆满了则扩容
        if (heapSize == heap.length) {
            int newCapacity = heap.length << 1;
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            }
            if (newCapacity == heap.length) {
                throw new IllegalStateException("Heap is full");
            }
            resizeHeap(heap, newCapacity);
        }

        // 添加到堆尾并上浮
        heap[heapSize] = node;
        siftUp(heap, /* index */ heapSize);
        heapSize++;
        heapNotEmpty.signal();
        nodeExpiredOrChanged.signal();
        log.debug("MemoryKvStore add node to heap: {}, heapSize: {}", node.key, heapSize);
    }

    private void removeFromHeap(Node<K, V> node) {
        // 如果堆大小不及容量的四分之一则缩容
        int newCapacity = heap.length >> 2;
        if (heapSize <= newCapacity && newCapacity >= INIT_HEAP_CAPACITY) {
            resizeHeap(heap, newCapacity);
        }

        // 从堆中移除，交换到堆尾并下沉
        if (node.index == heapSize - 1) {
            heapSize--;
            heap[heapSize] = null;
        } else {
            int idx = node.index;
            swap(heap, node.index, heapSize - 1);
            heapSize--;
            heap[heapSize] = null;
            siftDown(heap, idx, heapSize);
        }
        nodeExpiredOrChanged.signal();
    }

    private void siftUp(Node<K, V>[] heap, int i) {
        while (i > 0 && heap[i].expireTime < heap[parent(i)].expireTime) {
            swap(heap, i, parent(i));
            i = parent(i);
        }
        heap[i].index = i;
    }

    private void siftDown(Node<K, V>[] heap, int i, int size) {
        while (i < size) {
            int chosenIdx = i;
            if (leftChild(i) < size && heap[leftChild(i)].expireTime < heap[chosenIdx].expireTime) {
                chosenIdx = leftChild(i);
            }
            if (rightChild(i) < size && heap[rightChild(i)].expireTime < heap[chosenIdx].expireTime) {
                chosenIdx = rightChild(i);
            }
            if (chosenIdx == i) {
                break;
            }
            swap(heap, i, chosenIdx);
            i = chosenIdx;
        }
        heap[i].index = i;
    }

    private void swap(Node<K, V>[] heap, int i, int j) {
        assert heap[i].index == -1 || heap[i].index == i;
        assert heap[j].index == -1 || heap[j].index == j;
        heap[i].index = j;
        heap[j].index = i;
        Node<K, V> node = heap[i];
        heap[i] = heap[j];
        heap[j] = node;
    }

    private void resizeHeap(Node<K, V>[] heap, int newCapacity) {
        Node<K, V>[] newHeap = new Node[newCapacity];
        System.arraycopy(heap, 0, newHeap, 0, heapSize);
        this.heap = newHeap;
    }

    private int leftChild(int i) {
        return (i << 1) + 1;
    }

    private int rightChild(int i) {
        return (i << 1) + 2;
    }

    private int parent(int i) {
        return (i - 1) >> 1;
    }

    @Override
    public void close() throws Exception {
        clearThread.interrupt();
        clearThread = null;
    }

    @Data
    static class Node<K, V> {

        final K key;
        final V value;
        final long expireTime;
        /**
         * index为-1时表示不在堆中
         */
        volatile int index = -1;

    }

}
