/*
 * Copyright (c) 2020.
 */
package active.object;

public class DisplayStringRequest<T> extends MethodRequest<Object> {

    private final String string;

    protected DisplayStringRequest(Servant servant, String string) {
        super(servant, null);
        this.string = string;
    }

    @Override
    public void execute() {
        servant.displayString(string);
    }
}
