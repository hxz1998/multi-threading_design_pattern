/*
 * Copyright (c) 2020.
 */
package guarded.suspension;

import java.util.Random;

public class ServerThread extends Thread {
    private final RequestQueue requestQueue;
    private final Random random;

    public ServerThread(RequestQueue requestQueue, long seed, String name) {
        super(name);
        this.random = new Random(seed);
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Request request = requestQueue.getRequest();
            System.out.println(Thread.currentThread().getName() + " handles " + request);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
