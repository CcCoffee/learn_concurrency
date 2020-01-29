package com.learn.concurrency.confinement.date;

import com.learn.concurrency.annotation.Recommend;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 使用joda-time
 */
@Slf4j
@ThreadSafe
@Recommend
public class DateFormatByJodaTime {

    public static int client = 5000;
    public static int threadTotal = 200;

    public static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyyMMdd");

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(client);
        Semaphore semaphore = new Semaphore(threadTotal);
        for (int i = 0; i < client; i++) {
            final int count = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    parse(count);
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

    public static void parse(int i) {
        log.info("{} : {}", i, dateFormat.parseDateTime("20200129"));
    }
}
