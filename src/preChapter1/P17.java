/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package preChapter1;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class Bank {
    private String message = "还没消息来呢";

    public synchronized void write(String message) throws InterruptedException {
        this.message = message;
        System.out.println(Thread.holdsLock(this));
        wait();
        System.out.println(message);
    }

    public synchronized void read() {
        notifyAll();
        System.out.println(message);
    }
}

public class P17 {
    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();
        bank.write("hello");
        System.out.println(Thread.holdsLock(bank));
        ThreadFactory factory = Executors.defaultThreadFactory();
        factory.newThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        bank.write("Thread - 1 Running...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Thread.sleep(1000);
        factory.newThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    bank.read();
                }
            }
        }).start();
    }
}
