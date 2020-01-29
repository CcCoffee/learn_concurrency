package com.learn.concurrency.collections;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class UnsafeCollectionsExample {

    @NotThreadSafe
    @Test
    public void arrayListExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        ArrayList<Integer> list = new ArrayList<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    list.add(finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size : {}", list.size());
    }

    /**
     * 虽然Vector本身是线程安全的，但是这里是由先检查再执行非原子操作引发的线程不安全问题 ( if(condition(a)) {handle(a);} )
     *
     * @throws Exception
     */
    @NotThreadSafe
    @Test
    public void vectorMayNotSafeExample(){
        List<Integer> list = new Vector<>();
        while(true){
            for (int i = 0; i < 10; i++) {
                list.add(i);
            }
            new Thread(()->{
                for (int i = 0; i < list.size(); i++) {
                    list.remove(i);
                }
            }).start();
            new Thread(()->{
                for (int i = 0; i < list.size(); i++) {
                    list.get(i);//java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 17
                }
            }).start();
        }
    }

    @NotThreadSafe
    @Test
    public void hashSetExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        HashSet<Integer> set = new HashSet<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    set.add(finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("hashSet size : {}", set.size());
    }

    @NotThreadSafe
    @Test
    public void hashMapExample() throws Exception{
        //请求总数
        int requestCount = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        Map<Integer,Integer> map = new HashMap<>();
        //并发请求数
        int concurrentCount = 50;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    map.put(finalI,finalI);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("hashMap size : {}", map.size());
    }

    @NotThreadSafe
    @Test
    public void conditionExample() throws Exception{
        //请求总数
        int requestCount = 50000;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        //并发请求数
        int concurrentCount = 1000;
        Semaphore semaphore = new Semaphore(concurrentCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    if(finalI == 35000){
                        log.info("hit {}",finalI);//难以测试出多个线程进入
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
    }
}
