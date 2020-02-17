package com.learn.concurrency.collections;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ArrayListTest {

    /**
     * 向线程不安全的容器并发添加元素
     *
     * result : numbers size is 4993, count is 5000
     * @throws Exception 异常
     */
    @NotThreadSafe
    @Test
    public void test() throws Exception{
        ArrayList<Integer> numbers = new ArrayList<>();
        concurrentlyAdd(numbers);
    }

    /**
     * 向线程安全的容器并发添加元素
     *
     * @throws Exception 异常
     */
    @ThreadSafe
    @Test
    public void test2() throws Exception{
        Collection<Integer> numbers = Collections.synchronizedCollection(new ArrayList<>());
        concurrentlyAdd(numbers);
        Assert.assertEquals(5000L, numbers.size());
    }

    private void concurrentlyAdd(Collection<Integer> numbers) throws InterruptedException {
        // 最大并发线程数
        Semaphore semaphore = new Semaphore(50);
        CountDownLatch countDownLatch = new CountDownLatch(5000);
        AtomicInteger count = new AtomicInteger();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 5000; i++) {
            semaphore.acquire();
            executorService.submit(()->{
                numbers.add(count.incrementAndGet());
                semaphore.release();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Thread.sleep(5000);
        log.info("numbers size is {}, count is {}", numbers.size(), count.get());
    }
}
