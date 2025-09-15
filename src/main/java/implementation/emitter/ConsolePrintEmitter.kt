package implementation.emitter

import interpreter.PrintEmitter
import mock.StdOutputHandler;


class ConsolePrintEmitter : PrintEmitter {
    private val outputHandler = StdOutputHandler()

    override fun print(message: String) {
        outputHandler.print(message)
    }
}