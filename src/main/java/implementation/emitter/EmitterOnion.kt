package implementation.emitter
import interpreter.PrintEmitter
import mock.OutputHandler
class EmitterOnion(private val emitter: PrintEmitter): OutputHandler {

    override fun print(message: String) {
        val regex = Regex("""(.*?)(-?\d+(\.\d+)?)(\s*)$""")
        val formatted = regex.matchEntire(message)?.let { match ->
            val prefix = match.groupValues[1]
            val numberStr = match.groupValues[2]
            val suffix = match.groupValues[4]
            val num = numberStr.toDoubleOrNull()
            if (num != null) {
                prefix + (if (num % 1 == 0.0) num.toInt().toString() else num.toString()) + suffix
            } else {
                message
            }
        } ?: message
        emitter.print(formatted)

    }



}