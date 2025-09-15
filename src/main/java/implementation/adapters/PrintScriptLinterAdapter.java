package implementation.adapters;

import implementation.NotImplementedException;
import interpreter.PrintScriptLinter;
public class PrintScriptLinterAdapter implements PrintScriptLinter {
    @Override
    public void lint(java.io.InputStream src, String version, java.io.InputStream config, interpreter.ErrorHandler errorHandler) {
        throw new NotImplementedException("Needs implementation"); // TODO: implement
    }
}
