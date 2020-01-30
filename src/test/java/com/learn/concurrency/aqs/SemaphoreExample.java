package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class SemaphoreExample {

    /**
     * Semaphore的基本用法
     *
     * 每秒执行3个线程
     */
    @Test
    public void basic() throws InterruptedException {
        int threadNumber = 9;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                    TimeUnit.SECONDS.sleep(1);

                } catch (Exception e) {
                    log.error("error", e);
                } finally {
                    semaphore.release();
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(5000);// 给时间所有的子线程执行完
    }
    /**
     * Semaphore的批量获取许可
     *
     * 每秒执行1个线程
     */
    @Test
    public void batchAcquire() throws InterruptedException {
        int threadNumber = 9;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire(3);
                    log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                    TimeUnit.SECONDS.sleep(1);

                } catch (Exception e) {
                    log.error("error", e);
                } finally {
                    semaphore.release(3);
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(5000);// 给时间所有的子线程执行完
    }
    /**
     * 放弃执行未能获取许可的线程
     *
     * 仅执行了三个获取到许可的线程
     */
    @Test
    public void tryAcquire() throws InterruptedException {
        int threadNumber = 9;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    if(semaphore.tryAcquire()){
                        log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                        TimeUnit.SECONDS.sleep(1);
                        semaphore.release();
                    }
                } catch (Exception e) {
                    log.error("error", e);
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(5000);// 给时间所有的子线程执行完
    }
    /**
     * 超时时间内可以继续获取许可，放弃超时后未获得许可的线程
     *
     */
    @Test
    public void tryAcquireWithTimeout() throws InterruptedException {
        int threadNumber = 9;
        ExecutorService executorService = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    if(semaphore.tryAcquire(2000, TimeUnit.MILLISECONDS)){
                        log.info("i = {}, ThreadId = {}", finalI, Thread.currentThread().getId());
                        TimeUnit.SECONDS.sleep(1);
                        semaphore.release();
                    }
                } catch (Exception e) {
                    log.error("error", e);
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(5000);// 给时间所有的子线程执行完
    }

}
