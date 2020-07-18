/*
 * Copyright (c) 2020.
 */
package preChapter1;

public class TestJoin {
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1("abcde");
        thread1.start();
        try {
            thread1.join();
//            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            System.out.println("I am main Thread.");
        }
    }
}

class Thread1 extends Thread {
    public Thread1(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("I am " + getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
