/*
 * Copyright (c) 2020.
 */
package producer.consumer;

import java.util.Random;

public class MakerThread extends Thread {
    private final Table table;
    private final Random random;
    private static int id = 0;

    public MakerThread(String name, Table table, long seed) {
        super(name);
        random = new Random(seed);
        this.table = table;
    }

    @Override
    public void run() {
        while (true) {
            String cake = " " + nextId();
            try {
                // 不停的在这里生产着蛋糕，其中的 sleep 方法模拟的是生产耗时
                Thread.sleep(random.nextInt(1000));
                table.put(cake);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 把获取 id 的方法设置成线程安全的
    private synchronized int nextId() {
        return id++;
    }
}
