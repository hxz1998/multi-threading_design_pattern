/*
 * Copyright (c) 2020.
 */
package two.phase.termination;

public class CountUpThread extends Thread {
    // 用来判断是否开始了终止
    private volatile boolean shutdownRequested = false;

    // 用于计数
    private long count = 0;

    // 中断任务请求方法
    public void shutdownRequest() {
        shutdownRequested = true;
        interrupt();
    }

    // 是否终止线程
    public boolean isShutdownRequested() {
        return shutdownRequested;
    }

    @Override
    public final void run() {
        try {
            while (!shutdownRequested) {
                doWork();
            }
        } catch (InterruptedException e) {
        } finally {
            // 如果发生了异常，一定要完成清尾工作
            doShutdown();
        }
    }

    private void doWork() throws InterruptedException {
        count++;
        System.out.println("Working... count = " + count);
        Thread.sleep(500);
    }

    private void doShutdown() {
        System.out.println("Shutdown... count = " + count);
    }
}
