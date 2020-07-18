/*
 * Copyright (c) 2020.
 */
package immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class WriterThread extends Thread {
    private final List<Integer> list;

    public WriterThread(List<Integer> list) {
        super("Writer Thread");
        this.list = list;
    }

    @Override
    public void run() {
        for (int i = 0; true; i++) {
            list.add(i);
            list.remove(0);
        }
    }
}

class ReaderThread extends Thread {
    private final List<Integer> list;

    public ReaderThread(List<Integer> list) {
        super("Reader Thread");
        this.list = list;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (list) { // 获取了 list 的锁，开始线程同步
                for (int n : list) {
                    System.out.println(n);
                }
            }
        }
    }
}

public class P71 {
    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>();   // 一般的 ArrayList 是不安全的
//        List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>()); // 使用该方法后变成安全的
        List<Integer> list = new CopyOnWriteArrayList<>();  // 写时复制技术来避免冲突，该方法适用于频繁 读 操作，如果是频繁 写 ，那么就不合理
        new WriterThread(list).start();
        new ReaderThread(list).start();
    }
}
