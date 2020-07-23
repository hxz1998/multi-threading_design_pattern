/*
 * Copyright (c) 2020.
 */
package future;

public class RealData implements Data {
    private final String content;

    public RealData(char c, int count) {
        System.out.println("Making String...: " + count + " * " + c + " BEGIN...");
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            buffer[i] = c;
            try {
                Thread.sleep(100);  // 模拟费时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        content = String.valueOf(buffer);
        System.out.println("Making String : " + content + " END...");
    }

    @Override
    public String getContent() {
        return content;
    }
}
