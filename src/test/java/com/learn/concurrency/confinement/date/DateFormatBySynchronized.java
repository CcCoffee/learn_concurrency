package com.learn.concurrency.confinement.date;

import com.learn.concurrency.annotation.NotRecommend;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@ThreadSafe
@NotRecommend
public class DateFormatBySynchronized {

    public static int client = 5000;
    public static int threadTotal = 200;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(client);
        Semaphore semaphore = new Semaphore(threadTotal);
        for (int i = 0; i < client; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    parse();
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

    /**
     * 解决方案1.
     */
    public synchronized static void parse() {
        try {
            dateFormat.parse("20200129");
        } catch (ParseException e) {
            log.error("parse error : ", e);//java.lang.NumberFormatException: For input string: ""
        }
    }
}
