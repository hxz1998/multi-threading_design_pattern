/*
 * Copyright (c) 2020.
 */
package producer.consumer;

public class Table {
    private final String[] buffer;
    private int tail;
    private int head;
    private int count;

    public Table(int count) {
        this.buffer = new String[count];
        this.count = 0;
        this.head = 0;
        this.tail = 0;
    }

    // 放置蛋糕到 table 缓冲区
    public synchronized void put(String cake) throws InterruptedException {
        System.out.println("\t\t\t" + Thread.currentThread().getName() + " puts " + cake);
        // 如果缓冲区已经放满了，那么就让线程在这里等一会儿吧
        while (count >= buffer.length) {    // 守护条件
            wait();
        }
        buffer[tail] = cake;
        tail = (tail + 1) % buffer.length;
        count++;
        notifyAll();
    }

    // 从缓冲区取出来一个蛋糕
    public synchronized String get() throws InterruptedException {
        // 如果可用蛋糕数为 0 或者小于 0，那么就让线程等一小会儿
        while (count <= 0) {    // 守护条件
            wait();
        }
        String cake = buffer[head];
        head = (head + 1) % buffer.length;
        count--;
        // 唤醒其他程序
        notifyAll();
        System.out.println(Thread.currentThread().getName() + " gets " + cake);
        return cake;
    }
}
