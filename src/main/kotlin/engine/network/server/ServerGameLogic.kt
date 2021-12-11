package engine.network.server

import engine.network.common.MessageConsumer
import engine.network.common.NetMessage
import engine.network.server.ServerClient

interface ServerGameLogic {

    fun initialize(server: ServerInformation)

    fun update(updateTimeStep: Int)

    fun shutdown()

    fun clientMessageReceive(client: ServerClient, message: NetMessage)

}