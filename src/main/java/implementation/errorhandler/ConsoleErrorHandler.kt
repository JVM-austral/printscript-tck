package implementation.errorhandler
import interpreter.ErrorHandler

class ConsoleErrorHandler : ErrorHandler {
    override fun reportError(error: String) {
        System.err.println("Error: $error")
    }
}
