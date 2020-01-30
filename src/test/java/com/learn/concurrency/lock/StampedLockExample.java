package com.learn.concurrency.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

@Slf4j
public class StampedLockExample {

    private static int COUNT = 0;

    @Test
    public void writeLock() throws InterruptedException {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        StampedLock lock = new StampedLock();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    long stamp = lock.writeLock();
                    COUNT++;
                    lock.unlock(stamp);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.error("count = {}", COUNT);
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }
}
