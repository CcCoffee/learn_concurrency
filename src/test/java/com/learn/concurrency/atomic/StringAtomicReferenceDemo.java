package com.learn.concurrency.atomic;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用AtomicReference使不安全的字符串拼接变得线程安全
 */
@Slf4j
public class StringAtomicReferenceDemo {

    @NotThreadSafe
    @Test
    public void string() throws InterruptedException {
        AtomicReference<String> stringAtomicReference = new AtomicReference<>();
        stringAtomicReference.set("");
        Semaphore semaphore = new Semaphore(50);// 最大并发量为50
        int totalCount = 5000; // 总线程数
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalCount; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    String oldString = stringAtomicReference.get();
                    String newString = oldString.concat("a");
                    stringAtomicReference.compareAndSet(oldString, newString);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("length = {}", stringAtomicReference.get().length());
    }

    @ThreadSafe
    @Test
    public void atomicString() throws InterruptedException {
        AtomicReference<String> stringAtomicReference = new AtomicReference<>();
        stringAtomicReference.set("");
        Semaphore semaphore = new Semaphore(50);// 最大并发量为50
        int totalCount = 5000; // 总线程数
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalCount; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    // 采用死循环，线程过多时碰撞变多，性能变差
                    while (true) {
                        String oldString = stringAtomicReference.get();
                        String newString = oldString.concat("a"); // 局部变量时线程安全的
                        if(stringAtomicReference.compareAndSet(oldString, newString)){
                            break;
                        }
                    }
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("length = {}", stringAtomicReference.get().length());
    }
}
