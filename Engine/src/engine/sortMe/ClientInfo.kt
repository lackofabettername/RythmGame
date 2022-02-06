package engine.sortMe

import engine.application.RenderLogic
import engine.network.common.NetAddress
import engine.network.common.NetMessage

class ClientInfo(
    private val _client: Client,
    val RenderLogic: RenderLogic,
) {
    fun send(address: NetAddress, message: NetMessage) = _client.sendMessage(address, message)
}