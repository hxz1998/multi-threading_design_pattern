/*
 * Copyright (c) 2020.
 */
package balking;

import java.util.concurrent.TimeoutException;

public class P109 {
    public static void main(String[] args) {
        Host host = new Host(10000);
        try {
            System.out.println("BEGIN");
            host.execute();
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}

class Host {
    private final long timeout;
    private boolean ready = false;
    private int counter = 0;

    public Host(long timeout) {
        this.timeout = timeout;
    }

    public synchronized void setExecutable(boolean on) {
        this.ready = on;
        notifyAll();
    }

    public synchronized void execute() throws TimeoutException, InterruptedException {
        long start = System.currentTimeMillis();
        while (!ready) {
            long end = System.currentTimeMillis();
            long rest = timeout - (end - start);
            if (rest <= 0) {
                throw new TimeoutException("超时啦! end - start = " + (end - start) + ", timeout = " + timeout);
            }
            wait(rest);
        }
        doExecute();
    }

    private void doExecute() {
        System.out.println(Thread.currentThread().getName() + " calls execute.");
    }
}
