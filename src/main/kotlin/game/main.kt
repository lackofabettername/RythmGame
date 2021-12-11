package game

import engine.network.common.NetMessage
import engine.network.server.ServerClient
import engine.sortMe.Engine
import engine.network.server.ServerGameLogic
import engine.network.server.ServerInformation
import logging.Log
import logging.LogLevel

fun main() {
    Log.logLevel = LogLevel.Trace

    val serverGameLogic = object : ServerGameLogic {
        lateinit var server: ServerInformation

        override fun initialize(server: ServerInformation) {
            this.server = server
        }

        override fun update(updateTimeStep: Int) {
        }

        override fun shutdown() {
        }

        override fun clientMessageReceive(client: ServerClient, message: NetMessage) {
            Log.trace("ServerLogic", "Received $message from $client")
        }
    }

    val clientGameLogic = DummyClientLogic()
    val renderGameLogic = DummyRenderLogic(clientGameLogic)

    val engine = Engine(serverGameLogic, clientGameLogic, renderGameLogic)
    engine.run()
}