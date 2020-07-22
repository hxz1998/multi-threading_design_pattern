/*
 * Copyright (c) 2020.
 */
package worker.thread;

public class Channel {
    private static final int MAX_REQUEST = 100; // 请求队列容量
    private final Request[] requestQueue;       // 请求队列
    private int tail;       // 尾部指针，用来添加元素
    private int head;       // 头部指针，用来取出元素
    private int count;      // 记录当前队列有的请求数量

    private final WorkerThread[] threadPool;    // 工人线程池

    public Channel(int threads) {
        this.threadPool = new WorkerThread[threads];
        for (int i = 0; i < threads; i++)
            threadPool[i] = new WorkerThread("Worker-" + i, this);
        requestQueue = new Request[MAX_REQUEST];
        this.tail = 0;
        this.head = 0;
        this.count = 0;
    }

    public void startWorkers() {
        // 启动方法，把所有的工人线程都启动了
        for (WorkerThread worker : threadPool) {
            worker.start();
        }
    }

    public synchronized void putRequest(Request request) {
        try {
            while (count >= MAX_REQUEST)    // 如果队列已经满了，那就在这里一直等着吧
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        requestQueue[tail] = request;
        tail = (tail + 1) % MAX_REQUEST;
        count++;
        notifyAll();
    }

    public synchronized Request getRequest() {
        try {
            while (count <= 0)              // 如果队列是空的，那就等着放进来请求之后再处理吧
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Request request = requestQueue[head];
        head = (head + 1) % MAX_REQUEST;
        count--;
        notifyAll();
        return request;
    }
}
