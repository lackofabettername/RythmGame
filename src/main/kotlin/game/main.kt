package game

import engine.network.common.NetMessage
import engine.network.server.ServerClient
import engine.sortMe.Engine
import engine.network.server.ServerGameLogic
import engine.network.server.ServerInformation
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