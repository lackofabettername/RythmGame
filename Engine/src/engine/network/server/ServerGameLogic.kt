package engine.network.server

import engine.network.common.NetMessage

interface ServerGameLogic {

    fun initialize(server: ServerInformation)

    fun update(updateTimeStep: Int)

    fun shutdown()

    fun clientMessageReceive(client: ServerClient, message: NetMessage)

}