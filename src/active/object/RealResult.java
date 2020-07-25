/*
 * Copyright (c) 2020.
 */
package active.object;

public class RealResult<T> extends Result<T> {
    private final T resultValue;

    public RealResult(T resultValue) {
        this.resultValue = resultValue;
    }

    @Override
    public T getResultValue() {
        return this.resultValue;
    }
}
