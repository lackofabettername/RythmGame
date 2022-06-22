package engine.network.client

import engine.application.RenderLogic
import engine.network.common.NetAddress
import engine.network.common.NetMessage

class ClientInfo(
    private val _client: Client,
    val RenderLogic: RenderLogic,
) {
    val State: ClientState
        get() = _client.State

    val ServerAddress: NetAddress?
        get() = _client.ServerAddress

    fun send(address: NetAddress, message: NetMessage) = _client.sendMessage(address, message)

    /** Sends message to server */
    fun send(message: NetMessage) = _client.sendMessage(message)

    fun connect(address: NetAddress) = _client.connect(address)
}