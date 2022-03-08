package engine.network.client

import engine.application.RenderLogic
import engine.application.events.InputEvent
import engine.console.logging.Log
import engine.network.common.*

class Client(
    private val _network: NetManager,
    val Logic: ClientGameLogic,
    renderLogic: RenderLogic
) {

    var State = ClientState.Disconected
        private set

    //Todo: update this value
    var ServerAddress: NetAddress? = null
        private set

    init {
        Log.info("Client", "Initializing...")
        Logic.initialize(ClientInfo(this, renderLogic))
    }

    fun updateFrame(deltaTime: Long) {
        Logic.updateFrame(deltaTime)
    }

    fun shutdown() {
        Log.info("Client", "Shutting down...")
        Log.Indent++
        Logic.close()
        Log.Indent--
        Log.info("Client", "Shutdown complete.")
    }

    fun connect(address: NetAddress) {
        Log.trace("Client", "Sending connection request to $address...")
        sendMessage(
            address,
            NetMessage(
                NetMessageType.CL_CommandString,
                "Connect"
            )
        )
        State = ClientState.Waiting
    }

    fun sendMessage(address: NetAddress, message: NetMessage) {
        _network.sendPacket(
            NetPacket(
                address,
                NetAddress.loopbackClient, //Todo: change this?
                message,
                0,
                false
            )
        )
    }

    fun sendMessage(message: NetMessage) = sendMessage(ServerAddress!!, message)

    //region System event handlers

    fun onInputEvent(info: InputEvent) {
        //TODO: Pass the event to an input handler used by the game simulation?
    }

    fun onNetPacketReceived(packet: NetPacket) {
        Log.trace("Client", "Received $packet")

        val message = packet.Message

        if (State == ClientState.Waiting) {
            if (message.Type == NetMessageType.SV_CommandString) {
                if (message.Data != "Confirmed") {
                    State = ClientState.Disconected
                    Log.trace("Client", "Connection rejected!")
                } else {
                    State = ClientState.Connected
                    ServerAddress = packet.SenderAddress
                    Log.trace("Client", "Connection accepted!")
                }
            }
            return
        }

        if (State == ClientState.Connected && message.Type == NetMessageType.SV_GameState)
            State = ClientState.Active

        if (true) {
            Logic.MessageReceive(packet.Message)
        }

        //TODO:
        // Update last packet time.
        // Connectionless packet?
        // --- Handle the message elsewhere.
        // --- Once the message is handled, don't parse if not connected.
        // Validate message address?
        // --- Compare with net channel.
        // --- Disconnect if bad?
        // --- Don't parse this message if not valid.
        // Update internal sequence numbers?
//        ClientParse.serverMessage(packet.Message)

        //TODO: Record the message to a demo file?
    }

    //endregion
}