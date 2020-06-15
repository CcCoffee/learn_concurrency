package com.learn.concurrency.io;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadStatusTest {

    public static void main(String[] args) {
        ThreadPoolExecutor myThread = new ThreadPoolExecutor(3, 5, 40, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"MyThread" + count.getAndIncrement());
            }
        });

        myThread.submit(() -> {
                    InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        // ... handle IO exception
                    }
                });
        Object lock = new Object();
        myThread.submit(()->{
            try {
                lock.wait();
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                inputStreamReader.read();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        myThread.submit(()->{
            try {
                Thread.sleep(30000);
                System.out.println("ss");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }
}
