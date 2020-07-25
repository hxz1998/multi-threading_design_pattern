/*
 * Copyright (c) 2020.
 */
package active.object;

public abstract class MethodRequest<T> {
    protected final Servant servant;
    protected final FutureResult<T> futureResult;

    protected MethodRequest(Servant servant, FutureResult<T> futureResult) {
        this.futureResult = futureResult;
        this.servant = servant;
    }

    public abstract void execute();
}
