package com.learn.concurrency.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition作为条件类，很好的维护了一个等待信号的队列，并在适时的时候将节点移除并加入到AQS队列中，实现唤醒操作
 */
@Slf4j
public class ConditionExample {

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();

        new Thread(() -> {
            try {
                reentrantLock.lock(); // 1. 线程1获取锁，加入AQS等待队列
                log.info("wait signal");
                // 2. 首先线程从AQS队列移除了，对应的操作其实是锁的释放。接着进入Condition的等待队列里面去，该线程等待一个信号
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("get signal"); // 7. 线程1被唤醒后获取锁，然后执行
            reentrantLock.unlock();
        }).start();

        new Thread(() -> {
            reentrantLock.lock();  // 3. 线程2因为线程1释放锁被唤醒并判断它是否可以取到锁，于是线程2获取锁，也加入到了AQS等待队列
            log.info("get lock"); // 4
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 5. 线程2发送信号，这时候Condition队列里面有线程1的节点，于是它被取出加入到了AQS队列，
            // 此时线程1没有被唤醒，就只是放到了AQS队列中。因为在线程1成功恢复执行之前还必须获取锁，而此时锁还没有被线程2释放
            condition.signalAll();
            log.info("send signal ~ ");
            reentrantLock.unlock();//6. 线程2释放锁，AQS中目前只剩下线程1的节点，于是AQS释放锁按照从头到尾的顺序唤醒线程1，于是线程1继续开始执行
        }).start();
    }
}
