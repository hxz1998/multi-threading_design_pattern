/*
 * Copyright (c) 2020.
 */
package two.phase.termination;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main BEGIN : ");
        try {
            CountUpThread t = new CountUpThread();
            t.start();                                      // 启动计数线程
            Thread.sleep(10000);
            System.out.println("Main shutdownRequest...");
            t.shutdownRequest();                            // 中断计数线程

            System.out.println("Main Join...");
            t.join();                                       // 并入主线程，让主线程等待它的结束，然后大家一起结束
        } catch (InterruptedException e) {
        }
        System.out.println("Main End");
    }
}
