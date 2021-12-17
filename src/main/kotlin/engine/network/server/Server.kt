package engine.network.server

import engine.network.common.NetMessageType.*
import engine.network.common.NetAddress
import engine.network.common.NetManager
import engine.network.common.NetMessage
import engine.network.common.NetPacket
import logging.Log

class Server(
    private val _network: NetManager,
    private val _logic: ServerGameLogic
) {

    @Deprecated("can this be removed?")
    var dedicated = false

    val UpdateTimeStep = 100 //100ms so 10 ups

    private val _isRunning = false
    internal var _gameTime: Long = 0
    private var _gameTimeResidual: Long = 0

    private val _address = NetAddress.localServer
    internal val _session = ServerSession()

    init {
        Log.info("Server", "Initializing...")
        _logic.initialize(ServerInformation(this))
    }

    fun updateFrame(deltaTime: Long) {

        // Run the game in steps
        _gameTimeResidual += deltaTime
        while (_gameTimeResidual >= UpdateTimeStep) {
            _gameTime += UpdateTimeStep
            _gameTimeResidual -= UpdateTimeStep

            _logic.update(UpdateTimeStep)

            //TODO: Update the game here
        }
    }

    fun shutdown() {
        Log.info("Server", "Shutting down...")
        Log.Indent++

        _logic.shutdown()

        Log.Indent--
        Log.info("Server", "Shutdown complete.")
    }

    //region System event handlers
    fun onNetPacketReceived(packet: NetPacket) {
        Log.trace("Server", "Handling packet $packet")

        if (_session.getClient(packet.SenderAddress) == null)
            _session.addClient(packet.SenderAddress, ServerClient(packet.SenderAddress, this._address))

        // Find which client the message is from
        val client = _session.getClient(packet.SenderAddress)
        if (client != null) {
            assert(client.Address == packet.SenderAddress)

            // Make sure it's a valid, in-sequence packet
            if (client.Channel.process(packet)) {
                ServerParse.clientMessage(_logic, client, packet.Message)
            }
        } else { // Connectionless packet
            onNetPacketReceivedOOB(packet)
        }
    }

    private fun onNetPacketReceivedOOB(packet: NetPacket) {
        val message = packet.Message
        when (message.Type) {
            CL_CommandString -> {
                val commandText = packet.Message.Data as String
            }
            CL_UserCommand -> TODO()
        }
    }
    //endregion

    internal fun sendMessage(client: ServerClient, message: NetMessage) {
        val packet = client.Channel.send(message)
        _network.sendPacket(packet)
    }
}