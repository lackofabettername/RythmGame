package rythmGame

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel

fun main() {
    Log.LogLevel = LogLevel.Debug

    val server = ServerLogic()
    val client = ClientLogic()
    val application = Application()

    val engine = Engine(server, client, application)

    engine.run()
}