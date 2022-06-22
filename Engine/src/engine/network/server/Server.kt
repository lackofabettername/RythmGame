package engine.network.server

import engine.console.logging.Log
import engine.network.common.*
import engine.network.common.NetMessageType.CL_CommandString
import engine.network.common.NetMessageType.CL_UserCommand

class Server(
    private val _network: NetManager,
    val Logic: ServerGameLogic
) {

    @Deprecated("can this be removed?")
    var dedicated = false

    val UpdateTimeStep = 100 //100ms so 10 ups

    private val _isRunning = false
    internal var _gameTime = 0L
    private var _gameTimeResidual = 0L

    internal val _session = ServerSession()

    init {
        Log.info("Server", "Initializing...")
        Log.Indent++
        Logic.initialize(ServerInformation(this))
        Log.Indent--
        Log.info("Server", "Initialized.")
    }

    fun update(deltaTime: Long) {

        // Run the game in steps
        _gameTimeResidual += deltaTime
        while (_gameTimeResidual >= UpdateTimeStep) {
            _gameTime += UpdateTimeStep
            _gameTimeResidual -= UpdateTimeStep

            Logic.update(UpdateTimeStep)

            //TODO: Update the game here
        }
    }

    fun shutdown() {
        Log.info("Server", "Shutting down...")
        Log.Indent++

        Logic.shutdown()

        Log.Indent--
        Log.info("Server", "Shutdown complete.")
    }

    //region System event handlers
    fun onNetPacketReceived(packet: NetPacket) {
        Log.trace("Server", "Handling packet $packet")

        // Find which client the message is from
        val client = _session.getClient(packet.SenderAddress)
        if (client != null) {
            assert(client.Address == packet.SenderAddress)

            // Make sure it's a valid, in-sequence packet
            if (client.Channel.process(packet)) {
                ServerParse.clientMessage(Logic, client, packet.Message)
            }
        } else { // Connectionless packet
            onNetPacketReceivedOOB(packet)
        }
    }

    private fun onNetPacketReceivedOOB(packet: NetPacket) {
        when (packet.Message.Type) {
            CL_CommandString -> {
                val message = packet.Message.Data as String

                if (message == "Connect") {// New client
                    clientConnect(packet.SenderAddress, message)
                    return
                }

                TODO()
            }
            CL_UserCommand -> TODO()
            else -> TODO()
        }
    }

    private fun clientConnect(address: NetAddress, message: String) {
        Log.info("Server", "Client connecting...")
        val client = ServerClient(
            address,
            when (address.AddressType) {
                NetAddressType.Loopback -> NetAddress.loopbackServer
                NetAddressType.Internet -> NetAddress.localServer
                else -> NetAddress.invalid
            }
        )

        if (!Logic.clientConnect(client, message)) {
            Log.info("Server", "Client rejected!")
            return //Client rejected.
        }
        Log.info("Server", "Client accepted.")

        //Add client and send confirmation
        _session.addClient(address, client)
        sendMessage(
            client,
            NetMessage(
                NetMessageType.SV_CommandString,
                "Confirmed"
            )
        )
    }
    //endregion

    internal fun sendMessage(client: ServerClient, message: NetMessage) {
        val packet = client.Channel.getPacket(message)
        _network.sendPacket(packet)
    }
}