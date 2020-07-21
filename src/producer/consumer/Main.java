/*
 * Copyright (c) 2020.
 */
package producer.consumer;

public class Main {
    public static void main(String[] args) {
        Table table = new Table(3);
        new MakerThread("Maker - 1", table, 314159L).start();
        new MakerThread("Maker - 2", table, 123421L).start();
        new MakerThread("Maker - 3", table, 315713L).start();
        new EaterThread("Eater - 1", table, 314151L).start();
        new EaterThread("Eater - 2", table, 471514L).start();
        new EaterThread("Eater - 3", table, 314511L).start();

    }
}

