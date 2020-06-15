package com.learn.concurrency.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 40, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"MyThread");
            }
        });

        threadPoolExecutor.submit(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(4444);
                Socket clientSocket = serverSocket.accept();
            }catch (IOException e){}
        });
    }
}
