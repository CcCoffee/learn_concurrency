package com.learn.concurrency.aqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MutexTest {

    private Mutex mutex;

    @BeforeEach
    public void prepare(){
        mutex = new Mutex();
    }

    @Test
    void lock() {
        mutex.lock();
        assertTrue(mutex.isLock());
    }

    @Test
    void lockInterruptibly() {
        new Thread(()->assertThrows(InterruptedException.class,()->mutex.lockInterruptibly()),
                "Thread-A").start();

        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.currentThread().interrupt();
        },"Thread-B").start();
    }

    @Test
    void tryLock() {
        mutex.lock();
        assertFalse(mutex.tryLock());
    }

    @Test
    void tryLockWithTimeout() throws InterruptedException {
        mutex.lock();
        long startTime = System.currentTimeMillis();
        mutex.tryLock(3, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;
        long second = 1000L;
        assertTrue(duration >= 3 * second);
    }

    @Test
    void unlock() {
        mutex.lock();
        assertTrue(mutex.isLock());
        mutex.unlock();
        assertFalse(mutex.isLock());
    }
}