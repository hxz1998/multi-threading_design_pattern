/*
 * Copyright (c) 2020.
 */
package active.object;

public class FutureResult<T> extends Result<T> {
    private Result<T> result;
    private boolean ready;

    public synchronized void setResult(Result<T> result) {
        if (ready) return;
        this.result = result;
        ready = true;
        notifyAll();
    }

    @Override
    public synchronized T getResultValue() {
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result.getResultValue();
    }
}
