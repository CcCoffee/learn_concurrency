package com.learn.concurrency.collections;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class UnsafeCollectionsExample {

    @NotThreadSafe
    @Test
    public void arrayListExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        ArrayList<Integer> list = new ArrayList<>();
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
        log.info("size : {}", list.size());
    }

    @NotThreadSafe
    @Test
    public void hashSetExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        HashSet<Integer> set = new HashSet<>();
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
        log.info("hashSet size : {}", set.size());
    }

    @NotThreadSafe
    @Test
    public void hashMapExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Map<Integer,Integer> map = new HashMap<>();
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
        log.info("hashMap size : {}", map.size());
    }

    @NotThreadSafe
    @Test
    public void conditionExample() throws Exception{
        //请求总数
        int requestCount = 50000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        AtomicInteger count = new AtomicInteger(0);
        //并发请求数
        int concurrentCount = 1000;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            int finalI = i;
            executorService.execute(() -> {
                if(finalI == 35000){
                    log.info("hit {}",finalI);//难以测试出多个线程进入
                }
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
    }
}
