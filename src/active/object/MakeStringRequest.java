/*
 * Copyright (c) 2020.
 */
package active.object;

public class MakeStringRequest<T> extends MethodRequest<String> {

    private final int count;
    private final char fillchar;

    protected MakeStringRequest(Servant servant, FutureResult<String> futureResult, int count, char fillchar) {
        super(servant, futureResult);
        this.count = count;
        this.fillchar = fillchar;
    }

    @Override
    public void execute() {
        Result<String> result = servant.makeString(count, fillchar);
        futureResult.setResult(result);
    }
}
