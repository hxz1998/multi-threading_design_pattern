/*
 * Copyright (c) 2020.
 */
package producer.consumer;

import java.util.Random;
import java.util.concurrent.Exchanger;

class ProducerThread extends Thread {
    private final Exchanger<char[]> exchanger;
    private char[] buffer;
    private int index;
    private final Random random;

    public ProducerThread(Exchanger<char[]> exchanger, char[] buffer, long seed) {
        super("Producer Thread");
        this.exchanger = exchanger;
        this.buffer = buffer;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 生产者先往缓冲区填写内容，先把空空的缓冲区填写好，再申请和消费者切换
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = nextChar();
                    System.out.println(Thread.currentThread().getName() + ": " + buffer[i] + "->");
                }
                // 然后开始同消费者进行缓冲区切换
                System.out.println(Thread.currentThread().getName() + ": BEFORE exchange");
                buffer = exchanger.exchange(buffer);
                System.out.println(Thread.currentThread().getName() + ": AFTER exchange");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 获取下一个字符
    private char nextChar() throws InterruptedException {
        char c = (char) ('A' + index % 26);
        index++;
        Thread.sleep(random.nextInt(1000));
        return c;
    }
}

class ConsumerThread extends Thread {
    private final Exchanger<char[]> exchanger;
    private final Random random;
    private char[] buffer;

    public ConsumerThread(Exchanger<char[]> exchanger, char[] buffer, long seed) {
        super("Consumer Thread");
        this.exchanger = exchanger;
        this.buffer = buffer;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 首先对缓冲区进行切换，然后把内容满满的缓冲区拿过来进行输出
                System.out.println("\t\t\t" + Thread.currentThread().getName() + ": BEFORE exchange");
                buffer = exchanger.exchange(buffer);
                System.out.println(Thread.currentThread().getName() + ": AFTER exchange");
                for (int i = 0; i < buffer.length; i++) {
                    System.out.println("\t\t\t" + Thread.currentThread().getName() + ": -> " + buffer[i]);
                    Thread.sleep(random.nextInt(1000));
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class P133 {
    public static void main(String[] args) {
        Exchanger<char[]> exchanger = new Exchanger<>();
        char[] buffer1 = new char[10];
        char[] buffer2 = new char[10];
        new ProducerThread(exchanger, buffer1, 4314321L).start();
        new ConsumerThread(exchanger, buffer2, 3412351L).start();
    }
}
