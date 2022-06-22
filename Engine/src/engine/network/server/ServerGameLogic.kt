package engine.network.server

import engine.network.common.NetMessage

interface ServerGameLogic {
    fun initialize(server: ServerInformation)

    fun update(updateTimeStep: Int)

    fun clientMessageReceive(client: ServerClient, message: NetMessage)

    fun clientConnect(client: ServerClient, message: String): Boolean

    fun shutdown()
}