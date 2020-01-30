package com.learn.concurrency.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ReadWriteLockExample {

    private final Map<String, Data> map = new TreeMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();;
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public Data get(String key) {
        try{
            readLock.lock();
            return map.get(key);
        }finally {
            readLock.unlock();
        }
    }

    public void put(String key, Data data){
        try{
            writeLock.lock();
            map.put(key, data);
        }finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        ReadWriteLockExample example = new ReadWriteLockExample();
        example.put("a",new Data());
        log.info("data : {}", example.get("a"));
    }

    static class Data{

    }
}
