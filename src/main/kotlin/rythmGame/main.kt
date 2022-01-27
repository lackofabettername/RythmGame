package rythmGame

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel

fun main() {
    Log.LogLevel = LogLevel.Debug

    val application = Application()

    val engine = Engine(null, null, application)

    engine.run()
}