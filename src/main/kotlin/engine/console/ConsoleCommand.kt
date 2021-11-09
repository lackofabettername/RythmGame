package engine.console

import java.io.Serializable

class ConsoleCommand(
    val Text: String,
    vararg val Args: String
) : Serializable {
    override fun toString(): String {
        return "ConsoleCommand{$Text, $Args}"
    }
}