package com.learn.concurrency.confinement.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatByThreadLocal {
    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        };
    };

    public static void main(String[] args) {
        String[] waitingFormatTimeItems = { "2019-08-06", "2019-08-07", "2019-08-08" };
        for (int i = 0; i < waitingFormatTimeItems.length; i++) {
            final int i2 = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    for (int j = 0; j < 100; j++) {
                        String str = waitingFormatTimeItems[i2];
                        String str2 = null;
                        Date parserDate = null;
                        try {
                            parserDate = threadLocal.get().parse(str);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        str2 = threadLocal.get().format(parserDate);
                        System.out.println("i: " + i2 + "\tj: " + j + "\tThreadName: " + Thread.currentThread().getName() + "\t" + str + "\t" + str2);
                        if (!str.equals(str2)) {
                            throw new RuntimeException("date conversion failed after " + j + " iterations. Expected " + str + " but got " + str2);
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
