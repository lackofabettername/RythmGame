package engine.network.server

import engine.network.common.NetMessage

class ServerInformation(
    private val _server: Server
) {
    val UpdateTimeStep by _server::UpdateTimeStep

    var gameTime by _server::_gameTime
        private set

    val Session by _server::_session

    fun sendMessage(client: ServerClient, message: NetMessage) {
        _server.sendMessage(client, message)
    }
}