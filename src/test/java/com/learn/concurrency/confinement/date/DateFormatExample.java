package com.learn.concurrency.confinement.date;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@NotThreadSafe
public class DateFormatExample {

    public static int client = 5000;
    public static int threadTotal = 200;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(client);
        Semaphore semaphore = new Semaphore(threadTotal);
        for (int i = 0; i < client; i++) {
            semaphore.acquire();
            executorService.execute(() -> {
                parse();
            });
            semaphore.release();
            countDownLatch.countDown();
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    /**
     * SimpleDateFormat内部使用Calender对象进行日期操作，而Calendar内部存储的日期数据的全局变量
     * field，time等都是不安全的，更重要的Calendar内部函数操作对变量操作是不具有原子性的操作。
     * 例如ThreadB执行parse时会将ThreadA执行parse时的filed数组中间结果清空。
     *
     * 原因详见 ：https://www.cnblogs.com/yy3b2007com/p/11360895.html
     */
    public static void parse() {
        try {
            dateFormat.parse("20200129");
        } catch (ParseException e) {
            log.error("parse error : ", e);//java.lang.NumberFormatException: For input string: ""
        }
    }
}
