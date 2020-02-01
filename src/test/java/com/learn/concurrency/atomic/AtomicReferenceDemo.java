package com.learn.concurrency.atomic;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright © 2018 五月工作室. All rights reserved.
 *
 * @Project: tools
 * @ClassName: AtomicReferenceDemo
 * @Package: com.amos.tools.common.bean
 * @author: zhuqb
 * @Description: 主要用来展示AtomicReference使用方法
 * @date: 2019/9/11 0011 上午 9:46
 * @Version: V1.0
 *
 * @ModifyBy: Kevin Zheng
 * @ModifyDate: 2020-02-01
 */
@ThreadSafe
@Slf4j
public class AtomicReferenceDemo {

    private AtomicReference<Reference> atomicReference;

    /**
     * 构建器中初始化AtomicReference
     *
     * @param reference
     */
    public AtomicReferenceDemo(Reference reference) {
        this.atomicReference = new AtomicReference<>(reference);
    }

    public void atomicSelfIncrease() {
        Reference referenceOld;
        Reference referenceNew;

        long sequence;
        long count;
        long timestamp;

        while (true) {
            referenceOld = this.atomicReference.get();
            sequence = referenceOld.getSequence();
            sequence++;
            count = referenceOld.getCount();
            count++;
            timestamp = System.currentTimeMillis();

            referenceNew = new Reference(sequence, count, timestamp);// 局部变量，由于线程封闭，是线程安全的
            /**
             * 比较交换
             */
            if (this.atomicReference.compareAndSet(referenceOld, referenceNew)) {
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicReferenceDemo atomicReference =
                new AtomicReferenceDemo(new Reference(0, 0,System.currentTimeMillis()));
        Semaphore semaphore = new Semaphore(50);// 最大并发量为50
        int totalCount = 5000; // 总线程数
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalCount; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    atomicReference.atomicSelfIncrease();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("Reference = {}", atomicReference.atomicReference);
    }

    /**
     * 业务场景模拟
     * 序列需要自增并且时间需要更新成最新的时间戳
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Reference {
        /**
         * 序列
         */
        private long sequence;

        private long count;
        /**
         * 时间戳
         */
        private long timestamp;
    }
}

