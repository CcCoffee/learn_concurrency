package com.learn.concurrency;

public class TmpTest {
    public static void main(String[] args) {
        System.out.println(Integer.numberOfLeadingZeros(1));   // 31
        System.out.println(Integer.numberOfLeadingZeros(16));  // 27
        System.out.println(Integer.numberOfLeadingZeros(17));  // 27
        System.out.println(Integer.numberOfLeadingZeros(32));  // 26
    }
}
