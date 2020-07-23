/*
 * Copyright (c) 2020.
 */
package future;

public class FutureData implements Data {
    private RealData realData;
    private boolean ready = false;

    // 设置真正的数据
    public synchronized void setRealData(RealData realData) {
        if (ready) return;          // 如果已经设置过了，那就直接返回
        this.realData = realData;
        ready = true;
        notifyAll();                // 唤醒其他等待获取数据的线程
    }

    @Override
    public synchronized String getContent() {
        while (!ready) {    // 如果没有准备好数据，那就在这里等
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return realData.getContent();
    }
}
