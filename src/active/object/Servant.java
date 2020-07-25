/*
 * Copyright (c) 2020.
 */
package active.object;

public class Servant implements ActiveObject {

    @Override
    public Result<String> makeString(int count, char fillchar) {
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            buffer[i] = fillchar;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new RealResult<>(new String(buffer));
    }

    @Override
    public void displayString(String content) {
        try {
            System.out.println("Display String : " + content);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
