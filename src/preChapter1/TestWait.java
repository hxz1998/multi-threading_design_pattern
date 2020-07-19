/*
 * Copyright (c) 2020.
 */
package preChapter1;

public class TestWait {
    public synchronized void request() {
        System.out.println("准备开始 wait");
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("wait 结束了");
    }

    public synchronized void put() {
        System.out.println("准备开始唤醒其他程序");
        notifyAll();
        System.out.println("唤醒结束了");
    }
}

class Run extends Thread {
    private TestWait testWait;
    private int flag;

    public Run(TestWait testWait, int flag) {
        this.testWait = testWait;
        this.flag = flag;
    }

    @Override
    public void run() {
        if (flag == 1) {
            testWait.request();
        } else {
            testWait.put();
        }
    }

    public static void main(String[] args) {
        TestWait testWait = new TestWait();
        new Run(testWait, 1).start();
        new Run(testWait, 2).start();
    }
}