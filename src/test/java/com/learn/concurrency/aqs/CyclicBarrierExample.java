package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class CyclicBarrierExample {

    @Test
    public void basic() throws InterruptedException {
        int parties = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(parties);
        ExecutorService executorService = Executors.newCachedThreadPool();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread.sleep(1000);
            int finalI = i;
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000);
                    log.info("Thread {} 准备完毕", finalI);
                    cyclicBarrier.await();
                    log.info("Thread {} 线程恢复", finalI);
                } catch (Exception e) {
                    log.error("error", e);
                }
            });
        }
        Thread.sleep(12000);
        executorService.shutdown();
    }

    /**
     * 等待超时后的线程任务会直接停止等待，继续执行
     *
     * @throws InterruptedException
     */
    @Test
    public void timeout() throws InterruptedException {
        int parties = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(parties);
        ExecutorService executorService = Executors.newCachedThreadPool();
        int threadCount = 3;
        for (int i = 0; i < threadCount; i++) {
            Thread.sleep(1000);
            int finalI = i;
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000);
                    log.info("Thread {} 准备完毕", finalI);
                    try{
                        cyclicBarrier.await(2, TimeUnit.SECONDS);
                    }catch (TimeoutException | BrokenBarrierException e){
                        log.error("Thread {} error -> {}", finalI, e.getClass().getName());
                    }
                    log.info("Thread {} 线程恢复", finalI);
                } catch (Exception e) {
                    log.error("error", e);
                }
            });
        }
        Thread.sleep(12000);
        executorService.shutdown();
    }

    /**
     * 先执行callback再恢复线程
     * @throws InterruptedException
     */
    @Test
    public void callback() throws InterruptedException {
        int parties = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(parties,()->{
            log.info("call back...");
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread.sleep(1000);
            int finalI = i;
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000);
                    log.info("Thread {} 准备完毕", finalI);
                    cyclicBarrier.await();
                    log.info("Thread {} 线程恢复", finalI);
                } catch (Exception e) {
                    log.error("error", e);
                }
            });
        }
        Thread.sleep(12000);
        executorService.shutdown();
    }
}
