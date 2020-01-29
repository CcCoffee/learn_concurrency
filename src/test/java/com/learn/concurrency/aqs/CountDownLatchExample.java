package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CountDownLatchExample {

    /**
     * CountDownLatch的基本用法
     *
     * @throws InterruptedException
     */
    @Test
    public void basic() throws InterruptedException {
        int threadNumber = 200;
        CountDownLatch countDownLatch = new CountDownLatch(200);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error("error", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("complete");//必定是在所有子任务执行完成之后才执行的
        executorService.shutdown();
    }

    @Test
    public void timeout() throws InterruptedException {
        int threadNumber = 200;
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    Thread.sleep(100);
                    log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                } catch (Exception e) {
                    log.error("error", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(10,TimeUnit.MILLISECONDS);
        log.info("complete");//必定是在所有子任务执行完成之前执行的
        executorService.shutdown();
        Thread.sleep(1000);// 给时间所有的子线程执行完
    }
}
