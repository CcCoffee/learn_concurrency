package com.learn.concurrency.atomic;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
public class ABA {

    /**
     * ABA问题展示
     *
     * @throws Exception
     */
    @Test
    @NotThreadSafe
    public void unsafe() throws Exception{
        AtomicInteger count = new AtomicInteger(0);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(()->{
            try {
                Thread.sleep(500);
                countDownLatch.countDown();// 保证干扰线程执行完
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(true){
                if(count.compareAndSet(0, 1)){
                    log.info("主线程更新为 : {}", count.get());
                    break;
                }
            }
        });

        executorService.execute(()->{
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("干扰线程更新前count = {}", count.get());
            count.incrementAndGet();
            log.info("干扰线程自增后count = {}", count.get());
            count.decrementAndGet();
            log.info("干扰线程自减后count = {}", count.get());
        });
        executorService.shutdown();
        Thread.sleep(2000); // 保证任务执行完才结束进程
    }

    @Test
    @ThreadSafe
    public void safe() throws Exception{
        AtomicStampedReference<Integer> count = new AtomicStampedReference<>(0,0);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(()->{
            int originStamp = count.getStamp();
            try {
                Thread.sleep(500);
                countDownLatch.countDown();// 保证干扰线程执行完
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!count.compareAndSet(0, 1, originStamp, originStamp + 1)){
                log.info("主线程更新不成功");
            }
        });

        executorService.execute(()->{
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("干扰线程更新前count = {}", count.getReference());
            count.compareAndSet(0, 1, count.getStamp(), count.getStamp()+1);
            log.info("干扰线程自增后count = {}", count.getReference());
            count.compareAndSet(1, 0, count.getStamp(), count.getStamp()+1);
            log.info("干扰线程自减后count = {}", count.getReference());
        });
        executorService.shutdown();
        Thread.sleep(2000); // 保证任务执行完才结束进程
    }
}
