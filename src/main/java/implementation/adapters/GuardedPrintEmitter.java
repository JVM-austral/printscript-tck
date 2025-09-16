package implementation.adapters;
import interpreter.ErrorHandler;
import interpreter.PrintEmitter;

public class GuardedPrintEmitter implements PrintEmitter {
    private final PrintEmitter delegate;
    private final ErrorHandler handler;
    private boolean reported = false;

    public GuardedPrintEmitter(PrintEmitter delegate, ErrorHandler handler) {
        this.delegate = delegate;
        this.handler = handler;
    }

    @Override
    public void print(String message) {
        if (reported) {
            return;
        }
        try {
            delegate.print(message);
        } catch (OutOfMemoryError | StackOverflowError oom) {
            if (!reported) {
                reported = true;
                handler.reportError(oom.getMessage());
            }
        }
    }
}

