# Producer-Consumer 模式

## 0. 描述了什么事？
一边生产（Producer线程）数据（Data）一边消费（Consumer线程）数据（Data）。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`MakerThread`|“蛋糕”的生产者，不停的生产蛋糕，放到桌子上|
|`EaterThread`|”蛋糕“的消费者，不停的从桌子上取蛋糕吃|
|`Table`|桌子的抽象类，本质是缓冲区|

## 2. 示例程序

### 1. Main.java
```java

package producer.consumer;

public class Main {
    public static void main(String[] args) {
        Table table = new Table(3);
        new MakerThread("Maker - 1", table, 314159L).start();
        new MakerThread("Maker - 2", table, 123421L).start();
        new MakerThread("Maker - 3", table, 315713L).start();
        new EaterThread("Eater - 1", table, 314151L).start();
        new EaterThread("Eater - 2", table, 471514L).start();
        new EaterThread("Eater - 3", table, 314511L).start();

    }
}
```

### 2. MakerThread.java
```java
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
```

### 3. EaterThread.java
```java
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
```

### 4. Table.java
```java
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
```