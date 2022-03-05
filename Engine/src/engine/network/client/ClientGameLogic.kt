package engine.network.client

import engine.network.common.NetMessage

interface ClientGameLogic {

    fun initialize(clientInfo: ClientInfo)

    fun updateFrame(deltaTime: Long)

    fun close()

    fun MessageReceive(message: NetMessage)
}