package game

import engine.Engine
import logging.Log
import logging.LogLevel

fun main() {
    Log.LogLevel = LogLevel.Trace
    Log.IncludeTrace = false

    val serverGameLogic = DummyServerLogic()
    val clientGameLogic = DummyClientLogic()
    val renderGameLogic = DummyRenderLogic(clientGameLogic)

    val engine = Engine(serverGameLogic, clientGameLogic, renderGameLogic)
    engine.run()
}