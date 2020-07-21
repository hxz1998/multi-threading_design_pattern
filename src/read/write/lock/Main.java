/*
 * Copyright (c) 2020.
 */
package read.write.lock;

public class Main {
    public static void main(String[] args) {
        Data data = new Data(10);
        for (int i = 0; i < 6; i++) {
            new ReaderThread(data).start();
        }
        new WriterThread(data, "ABCDEFGHIJKLMNOPQRSTUVWXYZ").start();
        new WriterThread(data, "abcdefghijklmnopqrstuvwxyz").start();
    }
}
