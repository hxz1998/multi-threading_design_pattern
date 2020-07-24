/*
 * Copyright (c) 2020.
 */
package thread.specific.storage;

import java.io.IOException;
import java.io.PrintWriter;

public class ThreadSpecificLog {
    private PrintWriter writer;
    public ThreadSpecificLog (String filename) {
        try {
            writer = new PrintWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println(String content) {
        writer.println(content);
    }

    public void close() {
        writer.print("====== End of Log ======");
        writer.close();
    }
}
