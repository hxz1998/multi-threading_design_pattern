/*
 * Copyright (c) 2020.
 */
package worker.thread;

public class WorkerThread extends Thread {
    private final Channel channel;

    public WorkerThread(String name, Channel channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    public void run() {
        // 对于 Worker 来说，只知道 Request 有一个 execute 方法而已
        while (true) {
            Request request = channel.getRequest();
            request.execute();
        }
    }
}
