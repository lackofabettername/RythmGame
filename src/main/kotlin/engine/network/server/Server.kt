package engine.network.server

import engine.network.NetAddress
import engine.network.NetManager
import engine.network.NetMessage
import engine.network.NetPacket
import engine.sortMe.ServerGameLogic
import logging.Log.callerNameSimple
import logging.Log.info

class Server(
    private val _network: NetManager,
    private val _logic: ServerGameLogic?
) {

    @Deprecated("can this be removed?")
    var Dedicated = false

    var UpdateCount = 0
    var UpdateTimeStep = 10 //lack: 10ms? so 100 ups

    private val _isRunning = false
    private var _gameTime: Long = 0
    private var _gameTimeResidual: Long = 0

    private val _address = NetAddress.localServer()
    private val _session = ServerSession()

    fun updateFrame(deltaTime: Long) {
        // Run the game in steps
        _gameTimeResidual += deltaTime
        while (_gameTimeResidual >= UpdateTimeStep) {
            _gameTime += UpdateTimeStep.toLong()
            _gameTimeResidual -= UpdateTimeStep.toLong()
            //TODO: Update the game here
            ++UpdateCount
        }
    }

    fun shutdown() {
        info("Server", "Shutting down...")
        info("Server", "Shutdown complete.")
    }

    //region System event handlers
    fun onNetPacketReceived(packet: NetPacket) {

        // Find which client the message is from
        val client = _session.getClient(packet.Address)
        if (client != null) {
            assert(client.Address == packet.Address)

            // Make sure it's a valid, in-sequence packet
            if (client.Channel.process(packet)) {
                ServerParse.clientMessage(client, packet.Message)
            }
        } else { // Connectionless packet
            onNetPacketReceivedOOB(packet)
        }
    }

    private fun onNetPacketReceivedOOB(packet: NetPacket) {
        val commandText = packet.Message.Data as String
    }
    //endregion

    //endregion
    private fun sendMessage(client: ServerClient, message: NetMessage) {
        val packet = client.Channel.send(message)
        _network.sendPacket(packet)
    }
}