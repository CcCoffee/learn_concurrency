package com.learn.concurrency;

public class VolatileDemo {
    public static boolean finishFlag = false;

    //-server -Xcomp -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:CompileCommand=compileonly,*VolatileDemo.setFlag
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            int i = 0;
            while (!finishFlag){
                i++;
            }
        },"t1").start();
        Thread.sleep(1000);//确保t1先进入while循环后主线程才修改finishFlag
        finishFlag = true;
    }

}
