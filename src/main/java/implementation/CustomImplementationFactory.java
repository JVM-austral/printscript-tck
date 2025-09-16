package implementation;

import implementation.adapters.PrintScriptInterpreterAdapter;
import implementation.adapters.formatter.FormatterAdapter;
import implementation.adapters.linter.LinterAdapter;
import interpreter.PrintScriptFormatter;
import interpreter.PrintScriptInterpreter;
import interpreter.PrintScriptLinter;



public class CustomImplementationFactory implements PrintScriptFactory {

    @Override
    public PrintScriptInterpreter interpreter() {
        return new PrintScriptInterpreterAdapter();
    }

    @Override
    public PrintScriptFormatter formatter() {

        return new FormatterAdapter();
    }

    @Override
    public PrintScriptLinter linter() {
        // your PrintScript linter should be returned here.
        // make sure to ADAPT your linter to PrintScriptLinter interface.
        return new LinterAdapter();
    }
}