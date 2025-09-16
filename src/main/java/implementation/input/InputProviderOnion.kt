package implementation.input

import evaluator.input.InputProvider
import interpreter.InputProvider as InterpreterInputProvider

class InputProviderOnion(
    private val delegate: InterpreterInputProvider
) : InputProvider {

    override fun read(): String = delegate.input("input")
}
