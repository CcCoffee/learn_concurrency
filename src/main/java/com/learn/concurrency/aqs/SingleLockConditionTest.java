package com.learn.concurrency.aqs;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleLockConditionTest {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    LinkedList<String> list = new LinkedList<>();

    public void produce(String i){

        lock.lock();
        try {
            while(list.size() == 10){
                condition.await(); // 队列已满，让出锁
            }
            Thread.sleep(1000);
            list.add("production " + i);
            System.out.println(Thread.currentThread().getName() + " 生产了产品" + i);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void consume(){
        lock.lock();
        try{
             while (list.size() == 0){
                 condition.await();
             }
            String i = list.removeLast();
            System.out.println(Thread.currentThread().getName() + " 消费了产品" + i);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    static class Producer implements Runnable {

        private SingleLockConditionTest conditionTest;

        public Producer(SingleLockConditionTest conditionTest){
            this.conditionTest = conditionTest;
        }

        @Override
        public void run() {
            for (int i = 0; i < 200; i++) {
                conditionTest.produce(i+"");
            }
        }
    }

    static class Consumer implements Runnable{

        private SingleLockConditionTest conditionTest;

        public Consumer(SingleLockConditionTest conditionTest){
            this.conditionTest = conditionTest;
        }

        @Override
        public void run() {
            for (int i = 0; i < 200; i++) {
                conditionTest.consume();
            }
        }
    }

    public static void main(String[] args) {
        SingleLockConditionTest conditionTest = new SingleLockConditionTest();
        new Thread(new Producer(conditionTest)).start();
        new Thread(new Consumer(conditionTest)).start();
    }


}
