/*
 * Copyright (c) 2020.
 */
package thread.per.message;

public class Helper {
    public void handle(char c, int count) {
        System.out.println("\t\t handle(" + count + ", " + c + ") BEGIN");
        // 进行一些输出操作吧~
        for (int i = 0; i < count; i++) {
            System.out.print(c + " ");
            slowly();
        }
        System.out.println("\t\t handle(" + count + ", " + c + ") END");
    }

    private void slowly() {
        // 用来模拟耗时的操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
