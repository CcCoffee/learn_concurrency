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
import java.util.concurrent.atomic.*;

/**
 * 计数测试
 *
 * @author Kevin
 */
@Slf4j
public class CountExample {


    private static int COUNT = 0;
    private volatile static int V_COUNT = 0;
    private static AtomicInteger ATOMIC_INT_COUNT = new AtomicInteger(0);
    private static AtomicLong ATOMIC_LONG_COUNT = new AtomicLong(0);
    private static LongAdder LONG_ADDER_COUNT = new LongAdder();
    private static AtomicReference<Integer> ATOMIC_REF = new AtomicReference<>(0);
    private static AtomicBoolean HAS_HAPPENED = new AtomicBoolean(false);

    /**
     * 多个线程可能同时操作COUNT变量，而COUNT++并非原子操作
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
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    COUNT++;
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

    /**
     * 通过使用AtomicInteger使得线程独占ATOMIC_COUNT变量解决并发冲突问题
     *
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
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    ATOMIC_INT_COUNT.incrementAndGet();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, ATOMIC_INT_COUNT.get());
    }

    /**
     * 通过使用AtomicLong使得线程独占ATOMIC_LONG_COUNT变量解决并发冲突问题
     *
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
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    ATOMIC_LONG_COUNT.incrementAndGet();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, ATOMIC_LONG_COUNT.get());
    }

    /**
     * 通过使用LongAdder使得线程独占LONG_ADDER_COUNT变量解决并发冲突问题
     *
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
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    LONG_ADDER_COUNT.increment();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
        Assert.assertEquals(requestCount, LONG_ADDER_COUNT.longValue());
    }

    /**
     * AtomicReference没有提供类似于incrementAndGet这样的原子自增方法，
     * 所以不能用于计数这样自增的业务需求
     *
     * @throws Exception 忽略异常
     */
    @NotThreadSafe
    @Test
    public void wrongCountWithAtomicReference() throws Exception {
        long startTime = System.currentTimeMillis();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    ATOMIC_REF.compareAndSet(ATOMIC_REF.get(),1 + ATOMIC_REF.get()/*非原子*/);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.error("count = {}", ATOMIC_REF.get());
        log.info("total time = {} ms", System.currentTimeMillis() - startTime);
    }

    @ThreadSafe
    @Test
    public void countWithAtomicReferenced() {
        ATOMIC_REF.compareAndSet(0, 5);
        ATOMIC_REF.compareAndSet(3, 10);
        ATOMIC_REF.compareAndSet(5, 6);
        Assert.assertEquals(6, ATOMIC_REF.get().longValue());
    }

    /**
     * 必须是非static的volatile变量
     */
    private volatile int viewCount = 0;
    private static final AtomicIntegerFieldUpdater<CountExample> updater =
            AtomicIntegerFieldUpdater.newUpdater(CountExample.class,"viewCount");

    @ThreadSafe
    @Test
    public void countWithAtomicIntegerFieldUpdater() {
        CountExample countExample = new CountExample();
        updater.compareAndSet(countExample, 0, 10);
        updater.compareAndSet(countExample,0,11);
        if(updater.compareAndSet(countExample, 10, 1)){
            log.info("success 1: {}", countExample.viewCount);
        }
        if(updater.compareAndSet(countExample, 11, 8)){
            log.info("success 2: {}", countExample.viewCount);
        }else {
            log.error("failed : {}", countExample.viewCount);
        }
    }

    /**
     * 通过使用AtomicBoolean保证在高并发下代码只执行一次
     *
     * @throws Exception 忽略异常
     */
    @ThreadSafe
    @Test
    public void countWithAtomicBoolean() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    if(HAS_HAPPENED.compareAndSet(false,true)){
                        log.info("only execute once : {}", HAS_HAPPENED.get());
                    }
                    semaphore.release();
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }

    /**
     * volatile并不保证原子性，所以线程不安全，count的结果不一定为5000
     * volatile变量在各个线程的工作内存中不存在一致性问题，但Java运算并非原子操作，导致 volatile变量的运算在并发下一样是不安全的
     *
     * @throws Exception 忽略异常
     */
    @NotThreadSafe
    @Test
    public void volatileCount() throws Exception {
        long startTime = new Date().getTime();
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    V_COUNT++;
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.error("count = {}", V_COUNT);
        log.info("total time = {} ms", new Date().getTime() - startTime);
    }
}
