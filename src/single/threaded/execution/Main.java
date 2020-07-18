/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package single.threaded.execution;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing Gate, hit Ctrl + C to exit.");
        Gate gate = new Gate();
        new UserThread("Alice", "Australia", gate).start();
        new UserThread("Bob", "Brazil", gate).start();
        new UserThread("Chris", "China", gate).start();
    }
}
