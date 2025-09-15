package implementation.input

import evaluator.input.ConsoleInputProvider
import interpreter.InputProvider

class ConsoleInputProviderAdapter : InputProvider {
    private val consoleInput = ConsoleInputProvider()

    override fun input(prompt: String?): String {
        prompt?.let { print("$it ") }
        return consoleInput.read()
    }
}
