package com.learn.concurrency.collections;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SafeCollectionsExample {

    @ThreadSafe
    @Test
    public void useVectorInsteadOfArrayListExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Vector<Integer> list = new Vector<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            int finalI = i;
            executorService.execute(() -> {
                list.add(finalI);
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("Vector size : {}", list.size());
    }

    @ThreadSafe
    @Test
    public void useCollectionsInsteadOfHashSetExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Set<Integer> set = Collections.synchronizedSet(new HashSet<>());
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            int finalI = i;
            executorService.execute(() -> {
                set.add(finalI);
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("synchronizedSet size : {}", set.size());
    }

    @ThreadSafe
    @Test
    public void useHashTableInsteadOfHashMapExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Map<Integer,Integer> map = new Hashtable<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            int finalI = i;
            executorService.execute(() -> {
                map.put(finalI,finalI);
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("Hashtable size : {}", map.size());
    }

}
