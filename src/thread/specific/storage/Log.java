/*
 * Copyright (c) 2020.
 */
package thread.specific.storage;

public class Log {
    private static final ThreadLocal<ThreadSpecificLog> logCollection = new ThreadLocal<>();

    // 打印日志
    public static void println(String content) {
        getThreadSpecificLog().println(content);
    }

    public static void close() {
        getThreadSpecificLog().close();
    }

    private static ThreadSpecificLog getThreadSpecificLog() {
        ThreadSpecificLog threadSpecificLog = logCollection.get();
        if (threadSpecificLog == null) {
            threadSpecificLog = new ThreadSpecificLog(Thread.currentThread().getName() + "-log.txt");
            logCollection.set(threadSpecificLog);
        }
        return threadSpecificLog;
    }
}
