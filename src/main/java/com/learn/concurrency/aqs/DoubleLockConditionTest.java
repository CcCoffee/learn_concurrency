package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class DoubleLockConditionTest {

    private Lock producerLock = new ReentrantLock();
    private Lock consumerLock = new ReentrantLock();
    private Condition notFull = producerLock.newCondition();
    private Condition notEmpty = consumerLock.newCondition();
    private ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue<>();
    private AtomicInteger count = new AtomicInteger();

    public void produce(int i) {
        producerLock.lock();
        try {
            while (count.get() == 10) {
//                log.info("已满");
                notFull.await();
            }
            queue.add(i);
            Thread.sleep(500);
            log.info(" 放入一个商品库存，总库存为：{}", count.incrementAndGet());
//            if (count.get() < 10) {
//                notFull.signalAll();
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producerLock.unlock();
        }
        if (count.get() > 0) {
            try {
                consumerLock.lockInterruptibly();
                notEmpty.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                consumerLock.unlock();
            }
        }
    }

    public void consume() {
        consumerLock.lock();
        try {
            while (count.get() == 0) {
//                log.info("已空");
                notEmpty.await();
            }
            queue.remove();
            log.info(" 消费一个商品，总库存为：{}", count.decrementAndGet());
//            if (count.get() > 0) {
//                notEmpty.signalAll();
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            consumerLock.unlock();
        }
        if (count.get() < 10) {
            try {
                producerLock.lockInterruptibly();
                notFull.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                producerLock.unlock();
            }
        }
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                produce(i);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                consume();
            }
        }
    }

    public static void main(String[] args) {
        DoubleLockConditionTest conditionTest = new DoubleLockConditionTest();
        new Thread(conditionTest.new Producer()).start();
        new Thread(conditionTest.new Producer()).start();
        new Thread(conditionTest.new Consumer()).start();
        new Thread(conditionTest.new Consumer()).start();
    }

}
