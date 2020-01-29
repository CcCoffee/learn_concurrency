package com.learn.concurrency.collections;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ForEach {

    /**
     * 可采取先在循环内记录下标，再在循环之后通过下标移除元素的方式
     * @param list
     */
    public static void for1(List<Integer> list){
        int index = 0;
        for (Integer i :
                list) {
            if(index == 2){
                list.remove(index);//java.util.ConcurrentModificationException
            }
            index ++;
        }
    }


    public static void for2(List<Integer> list){
        for (int i = 0; i < list.size(); i++) {
            if(i == 2){
                list.remove(i);//success
            }
        }
    }

    /**
     * 可采取先在循环内记录下标，再在循环之后通过下标移除元素的方式
     * @param list
     */
    public static void iter(List<Integer> list){
        Iterator<Integer> iterator = list.iterator();
        while(iterator.hasNext()){
            Integer i = iterator.next();
            if(i.equals(2)){
                list.remove(i);//java.util.ConcurrentModificationException
            }
        }
    }

    public static void main(String[] args) {
        Vector<Integer> list = new Vector<>();
        list.add(1);
        list.add(2);
        list.add(3);
        for1(list);
        for2(list);//success
        iter(list);
    }
}
