package implementation.adapters;

import evaluator.input.ConsoleInputProvider;
import interpreter.ErrorHandler;
import interpreter.InputProvider;
import interpreter.PrintEmitter;
import interpreter.PrintScriptInterpreter;
import mock.StdOutputHandler;
import runner.RunnerImplementation;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PrintScriptInterpreterAdapter implements PrintScriptInterpreter {

    @Override
    public void execute(
            InputStream src,
            String version,
            PrintEmitter emitter,
            ErrorHandler handler,
            InputProvider provider
    ) {
        try {

            String runnerVersion = version.equals("1.0") ? "V1" : "V2";
            RunnerImplementation runner = new RunnerImplementation(runnerVersion, new StdOutputHandler(), new ConsoleInputProvider());


            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;

            List<String> lines = new ArrayList<>();
            StringBuilder lineBuffer = new StringBuilder();

            OutputStream capturingStream = new OutputStream() {
                @Override
                public void write(int b) {
                    char c = (char) b;
                    if (c == '\n') {
                        lines.add(lineBuffer.toString());
                        lineBuffer.setLength(0);
                    } else if (c != '\r') {
                        lineBuffer.append(c);
                    }
                }
            };

            try {
                System.setOut(new PrintStream(capturingStream, true, StandardCharsets.UTF_8));
                System.setErr(new PrintStream(capturingStream, true, StandardCharsets.UTF_8));

                runner.run(src);
            } finally {
                // Flush last partial line
                if (lineBuffer.length() > 0) {
                    lines.add(lineBuffer.toString());
                }
                System.setOut(originalOut);
                System.setErr(originalErr);
            }

            String errorHeader = "Printing errors found during execution:";
            boolean afterErrorHeader = false;
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                } else if (line.equals(errorHeader)) {
                    afterErrorHeader = true;
                } else if (afterErrorHeader) {
                    handler.reportError(line);
                } else {
                    emitter.print(line);
                }
            }
        } catch (Exception e) {
            handler.reportError("Interpreter error: " + e.getMessage());
        }
    }
}