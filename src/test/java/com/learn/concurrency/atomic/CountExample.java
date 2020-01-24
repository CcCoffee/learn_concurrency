package com.learn.concurrency.atomic;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 计数测试
 *
 * @author Kevin
 */
@Slf4j
public class CountExample {


    private static int COUNT = 0;
    private static AtomicInteger ATOMIC_INT_COUNT = new AtomicInteger(0);
    private static AtomicLong ATOMIC_LONG_COUNT = new AtomicLong(0);
    private static LongAdder LONG_ADDER_COUNT = new LongAdder();

    /**
     * 多个线程可能同时操作COUNT变量，而COUNT++并非原子操作
     * @throws Exception 忽略异常
     */
    @NotThreadSafe
    @Test
    public void count() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            executorService.execute(() -> COUNT++);
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.error("count = {}", COUNT);
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }

    /**
     * 通过使用AtomicInteger使得线程独占ATOMIC_COUNT变量解决并发冲突问题
     * @throws Exception 忽略异常
     */
    @ThreadSafe
    @Test
    public void countWithAtomicInteger() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            executorService.execute(() -> ATOMIC_INT_COUNT.incrementAndGet());
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, ATOMIC_INT_COUNT.get());
    }

    /**
     * 通过使用AtomicLong使得线程独占ATOMIC_LONG_COUNT变量解决并发冲突问题
     * @throws Exception 忽略异常
     */
    @ThreadSafe
    @Test
    public void countWithAtomicLong() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            executorService.execute(() -> ATOMIC_LONG_COUNT.incrementAndGet());
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, ATOMIC_LONG_COUNT.get());
    }

    /**
     * 通过使用LongAdder使得线程独占LONG_ADDER_COUNT变量解决并发冲突问题
     * @throws Exception 忽略异常
     */
    @ThreadSafe
    @Test
    public void countWithLongAdder() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            semaphore.acquire();
            executorService.execute(() -> LONG_ADDER_COUNT.increment());
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, LONG_ADDER_COUNT.longValue());
    }
}
