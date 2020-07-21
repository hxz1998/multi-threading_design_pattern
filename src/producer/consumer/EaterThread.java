/*
 * Copyright (c) 2020.
 */
package producer.consumer;

import java.util.Random;

public class EaterThread extends Thread {
    private final Random random;
    private final Table table;

    public EaterThread(String name, Table table, long seed) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 不停的从桌子上拿蛋糕吃，其中的 sleep 方法模拟的是吃的过程
                String cake = table.get();
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
