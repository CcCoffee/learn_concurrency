package com.learn.concurrency.collections.concurrent;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class SafeCollectionsExample {

    @ThreadSafe
    @Test
    public void copyOnWriteArrayListExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        List<Integer> list = new CopyOnWriteArrayList<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    list.add(finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size : {}", list.size());
    }

    @ThreadSafe
    @Test
    public void copyOnWriteArraySetExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Set<Integer> list = new CopyOnWriteArraySet<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    list.add(finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size : {}", list.size());
    }

    /**
     * HashMap -> ConcurrentHashMap
     *
     * @throws Exception
     */
    @ThreadSafe
    @Test
    public void concurrentHashMapExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Map<Integer,Integer> map = new ConcurrentHashMap<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    map.put(finalI,finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size : {}", map.size());
    }

    /**
     * TreeMap -> ConcurrentSkipListMap
     *
     * @throws Exception
     */
    @ThreadSafe
    @Test
    public void concurrentSkipListMapExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Map<Integer,Integer> map = new ConcurrentSkipListMap<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    map.put(finalI,finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size : {}", map.size());
    }
}
