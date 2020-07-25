/*
 * Copyright (c) 2020.
 */
package active.object;

public class ActivationQueue {
    private static final int MAX_COUNT = 100;
    private final MethodRequest[] requests = new MethodRequest[MAX_COUNT];
    private int tail, head, count;

    public ActivationQueue() {
        tail = 0;
        head = 0;
        count = 0;
    }

    public synchronized void putRequest(MethodRequest request) {
        while (count >= MAX_COUNT) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        requests[tail] = request;
        tail = (tail + 1) % MAX_COUNT;
        count++;
        notifyAll();
    }

    public synchronized MethodRequest takeRequest() {
        while (count <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MethodRequest request = requests[head];
        head = (head + 1) % MAX_COUNT;
        count--;
        notifyAll();
        return request;
    }
}
