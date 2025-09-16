package implementation.adapters;

import ast.Ast;
import implementation.emitter.EmitterOnion;
import implementation.input.InputProviderOnion;
import errorhandler.MockErrorHandler;
import interpreter.ErrorHandler;
import interpreter.InputProvider;
import interpreter.PrintEmitter;
import interpreter.PrintScriptInterpreter;
import runner.RunnerImplementation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PrintScriptInterpreterAdapter implements PrintScriptInterpreter {

    @Override
    public void execute(
            InputStream src,
            String version,
            PrintEmitter emitter,
            ErrorHandler handler,
            InputProvider provider
    ) {


        String ver;
        if (version.equals("1.0")) {
            ver = "V1";
        } else if (version.equals("1.1")) {
            ver = "V2";
        } else {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }

        var outputHandler = new EmitterOnion(emitter);
        var inputProvider = new InputProviderOnion(provider);
        Map<String, Ast> env = new HashMap<>();
        System.getenv().forEach((key, value) -> {
            env.put(key, new ast.StringLiteral(value, 0, 0));
        });

        var runner = new RunnerImplementation(ver, outputHandler, inputProvider,env);
        try {
            runner.run(src);
        } catch (OutOfMemoryError e) {
            handler.reportError("Java heap space");
            return;
        }

        MockErrorHandler runnerErrorHandler = runner.getErrorHandler();

        var errors = runnerErrorHandler.getCapturedErrors();

        for (var error : errors) {
            handler.reportError(error);
        }
    }
}