package game

import engine.Engine
import game.meBored.MeBored
import logging.Log
import logging.LogLevel

fun main() {
    Log.LogLevel = LogLevel.Debug
    Log.IncludeTrace = false

    val serverGameLogic = DummyServerLogic()
    val clientGameLogic = DummyClientLogic()
    //val renderGameLogic = DummyRenderLogic(clientGameLogic)
    val renderGameLogic = MeBored(clientGameLogic)

    val engine = Engine(null, null, renderGameLogic)
    engine.run()
}