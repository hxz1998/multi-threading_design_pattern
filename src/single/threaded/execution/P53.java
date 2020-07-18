/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package single.threaded.execution;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 该程序描述的是，十个线程来共享三个资源的例子。
 * 其中，使用 Semaphore 来表示信号量
 * 使用 permits 来表示一共可用资源数
 */
class Log {
    public static void println(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }
}

class BoundedResource {
    private final Semaphore semaphore;
    private final int permits;
    private final static Random random = new Random(314159);

    public BoundedResource(int permits) {
        this.semaphore = new Semaphore(permits);
        this.permits = permits;
    }

    public void use() throws InterruptedException {
        // 使用资源，当没有可使用的资源时，就会在这里阻塞
        semaphore.acquire();
        try {
            doUse();
        } finally {
            // 无论结果如何，一定要释放掉资源
            semaphore.release();
        }
    }

    public void doUse() throws InterruptedException {
        Log.println("Begin: used = " + (permits - semaphore.availablePermits()));
        Thread.sleep(random.nextInt(500));
        Log.println("End: used = " + (permits - semaphore.availablePermits()));
    }
}

class UseThread extends Thread {
    private final static Random random = new Random(26535);
    private final BoundedResource boundedResource;

    public UseThread(BoundedResource boundedResource) {
        this.boundedResource = boundedResource;
    }

    public void run() {
        try {
            while (true) {
                boundedResource.use();
                Thread.sleep(random.nextInt(3000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class P53 {
    public static void main(String[] args) {
        BoundedResource resource = new BoundedResource(3);
        for (int i = 0; i < 10; i++) {
            new UseThread(resource).start();
        }
    }
}
