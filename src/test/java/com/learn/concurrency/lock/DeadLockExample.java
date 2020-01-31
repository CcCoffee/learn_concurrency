package com.learn.concurrency.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DeadLockExample implements Runnable{

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();
    private int flag;

    private DeadLockExample(int flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        if(flag == 0){
            synchronized (lock1){
                try {
                    Thread.sleep(1000);
                    log.info("Thread 1 get lock1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2){
                    log.info("Thread 1 get lock2");
                }
            }
        }else{
            synchronized (lock2){
                try {
                    Thread.sleep(1000);
                    log.info("Thread 2 get lock2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1){
                    log.info("Thread 2 get lock1");
                }
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new DeadLockExample(0));
        executorService.submit(new DeadLockExample(1));
        executorService.shutdown();
    }
}
