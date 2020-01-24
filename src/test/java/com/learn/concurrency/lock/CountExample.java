package com.learn.concurrency.lock;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 使用锁进行计数
 */
@Slf4j
public class CountExample {

    private static int COUNT = 0;

    /**
     * 使用synchronized保证同步
     *
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
            executorService.execute(() -> {
                synchronized (this){
                    COUNT++;
                }
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
        log.error("count = {}", COUNT);
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }
}
