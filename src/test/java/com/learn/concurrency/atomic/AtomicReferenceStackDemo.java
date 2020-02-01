package com.learn.concurrency.atomic;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@ThreadSafe
@Slf4j
public class AtomicReferenceStackDemo {

    AtomicReference<Node> top = new AtomicReference<Node>();
    public void push(String item){
        Node newTop = new Node(item);
        Node oldTop;
        do{
            oldTop = top.get();
            newTop.next = oldTop;
        }
        while(!top.compareAndSet(oldTop, newTop));
    }
    public String pop(){
        Node newTop;
        Node oldTop;
        do{
            oldTop = top.get();
            if(oldTop == null){
                return null;
            }
            newTop = oldTop.next;
        }
        while(!top.compareAndSet(oldTop, newTop));
        return oldTop.item;
    }

    /**
     * 虽然AtomicReferenceStackDemo是线程安全类，但是如果调用代码写的线程不安全，最终的结果
     * 也是线程不安全的。在这里外部调用代码的线程安全是通过使用AtomicInteger来保证的。
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        AtomicReferenceStackDemo stack = new AtomicReferenceStackDemo();
        Semaphore semaphore = new Semaphore(50);// 最大并发量为50
        int totalPushCount = 5000; // 总线程数
        CountDownLatch countDownLatch = new CountDownLatch(totalPushCount);
        ExecutorService executorService = Executors.newCachedThreadPool();
        AtomicInteger item = new AtomicInteger(0);
        for (int i = 0; i < totalPushCount; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    stack.push(String.valueOf(item.incrementAndGet()));
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("After push : top node item = {}", stack.top.get().item);// item应该为5000

        int totalPopCount = totalPushCount - 1;
        Semaphore semaphore2 = new Semaphore(50);// 最大并发量为50
        CountDownLatch countDownLatch2 = new CountDownLatch(totalPopCount);
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        for (int i = 0; i < totalPopCount; i++) {
            executorService2.execute(()->{
                try {
                    semaphore2.acquire();
                    stack.pop();
                    semaphore2.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch2.countDown();
            });
        }
        countDownLatch2.await();
        executorService2.shutdown();
        log.info("After pop : top node item = {}", stack.top.get().item);//保留一个元素，item应该为1
    }

    @Data
    @NoArgsConstructor
    static class Node {
        private String item;
        private Node next;
        public Node(String item){
            this.item = item;
        }
    }
}

