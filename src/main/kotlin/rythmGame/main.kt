package rythmGame

import engine.Engine
import engine.console.logging.Log
import engine.console.logging.LogLevel
import rythmGame.simulation.ClientLogic
import rythmGame.rendering.MainRenderLogic
import rythmGame.simulation.ServerLogic

fun main() {
    Log.LogLevel = LogLevel.Trace

    val server = ServerLogic()
    val client = ClientLogic()
    val renderer = MainRenderLogic(client)

    val engine = Engine(server, client, renderer)

    engine.run()
}