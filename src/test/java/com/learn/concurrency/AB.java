package com.learn.concurrency;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 性能测试
 * 模拟AB进行性能测试
 *
 * @author Kevin
 */
@Slf4j
public class AB {

    private static int COUNT = 0;

    @NotThreadSafe
    @Test
    public void test() throws Exception {
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
                COUNT++;
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        log.info("count = {}", COUNT);
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }
}
