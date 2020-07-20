/*
 * Copyright (c) 2020.
 */
package balking;

public class Main {
    public static void main(String[] args) {
        Data data = new Data("data.txt", "(empty)");
        ChangerThread changer = new ChangerThread("ChangerThread", data);
        SaverThread saver = new SaverThread("SaverThread", data);
        changer.start();
        saver.start();
    }
}
