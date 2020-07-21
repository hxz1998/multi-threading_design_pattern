/*
 * Copyright (c) 2020.
 */
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
