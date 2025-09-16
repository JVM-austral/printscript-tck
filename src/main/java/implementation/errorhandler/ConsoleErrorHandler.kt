package implementation.errorhandler
import errorhandler.MockErrorHandler
import interpreter.ErrorHandler


class ConsoleErrorHandler(private val handler: MockErrorHandler) : ErrorHandler {
    override fun reportError(error: String) {
        System.err.println("Error: $error")
    }

}

