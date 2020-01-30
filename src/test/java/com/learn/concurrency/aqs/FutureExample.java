package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j
public class FutureExample {

    class MyCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            log.info("do something in callable");
            Thread.sleep(5000);
            return "Done";
        }
    }

    @Test
    public void future() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(new MyCallable());
        log.info("do something in main");
        Thread.sleep(1000);
        String result = future.get();//阻塞方法
        log.info("result : {}", result);
    }

    /**
     * 使用FutureTask实现与上面相同的功能
     * @throws Exception
     */
    @Test
    public void futureTask() throws Exception{
        FutureTask<String> futureTask = new FutureTask<>(()->{
            log.info("do something in callable");
            Thread.sleep(5000);
            return "Done";
        });
        new Thread(futureTask).start();
        log.info("do something in main");
        Thread.sleep(1000);
        String result = futureTask.get();//阻塞方法
        log.info("result : {}", result);
    }
}
