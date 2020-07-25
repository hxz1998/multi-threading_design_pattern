/*
 * Copyright (c) 2020.
 */
package active.object;

public interface ActiveObject {
    Result<String> makeString(int count, char fillchar);
    void displayString(String content);
}
