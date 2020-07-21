# Read-Write Lock 模式

## 0. 描述了什么事？

使用一个人来专门控制锁。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`ReadWriteLock`|读写锁类，该类提供了用于读取和写入的锁|
|`Data`|数据的抽象类，保存 `ReadWriteLock` 的实例|
|`ReaderThread`|保存 `Data` 的实例，并不断读取其内容|
|`WriterThread`|保存 `Data` 的实例，并不断更新其内容|

## 2. 程序示例

### 1. Main.java
```java
package read.write.lock;

public class Main {
    public static void main(String[] args) {
        Data data = new Data(10);
        for (int i = 0; i < 6; i++) {
            new ReaderThread(data).start();
        }
        new WriterThread(data, "ABCDEFGHIJKLMNOPQRSTUVWXYZ").start();
        new WriterThread(data, "abcdefghijklmnopqrstuvwxyz").start();
    }
}
```

### 2. ReadWriteLock.java
```java
package read.write.lock;

public class ReadWriteLock {
    private int readingReaders = 0;
    private int writingWriters = 0;
    private int waitingWriters = 0;
    private boolean preferWriter = true;

    public synchronized void readLock() throws InterruptedException {
        // 判断是否有线程正在进行写操作呢，如果有的话，那就等等吧
        // 如果优先写操作，而且有写线程在等待，那么也就先让人家写吧
        while (writingWriters > 0 || (preferWriter && waitingWriters > 0)) {
            wait();
        }
        // 读取线程计数加一
        readingReaders++;
    }

    public synchronized void readUnlock() {
        // 释放掉读取锁，并且唤醒其他等待的线程，这里唤醒的是写线程
        readingReaders--;
        preferWriter = true;
        notifyAll();
    }

    public synchronized void writeLock() throws InterruptedException {
        // 首先把写线程放到队列中
        waitingWriters++;
        try {
            // 然后判断是否有人正在写或者正在读，如果有的话，那就需要等等再写
            while (writingWriters > 0 || readingReaders > 0) {
                wait();
            }
        } finally {
            // 不管写成功没有，都必须把等待线程给从等待队列中去除掉
            waitingWriters--;
        }
        // 开始写了！
        writingWriters++;
    }

    public synchronized void writeUnlock() {
        // 释放掉写入锁，并且唤醒其他的写入锁和读取锁
        writingWriters--;
        preferWriter = false;
        notifyAll();
    }
}
```

### 3. ReaderThread.java
```java
package read.write.lock;

public class ReaderThread extends Thread {
    private final Data data;

    public ReaderThread(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char[] buffer = data.read();
                System.out.println(Thread.currentThread().getName() + " reads " + String.valueOf(buffer));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 4. WriterThread.java
```java
package read.write.lock;

import java.util.Random;

public class WriterThread extends Thread {
    private static final Random random = new Random();
    private final Data data;
    private final String filler;
    private int index = 0;

    public WriterThread(Data data, String filler) {
        this.data = data;
        this.filler = filler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char c = nextChar();
                data.write(c);
                Thread.sleep(random.nextInt(3000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private char nextChar() {
        char c = filler.charAt(index);
        index = (++index) % filler.length();
        return c;
    }
}
```