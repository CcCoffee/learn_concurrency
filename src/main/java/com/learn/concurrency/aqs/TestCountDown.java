package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class TestCountDown {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread t1 = new Thread(()->{
            try {
                log.info("t1 start--------");
                countDownLatch.await();
                Thread.sleep(2000);
                log.info("t1 return --------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(()->{
            try {
                log.info("t2 start--------");
                Thread.sleep(2000);
                countDownLatch.countDown();
                log.info("t2 return --------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t3 = new Thread(()->{
            try {
                log.info("t3 start--------");
                Thread.sleep(2000);
                countDownLatch.countDown();
                log.info("t3 return --------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
