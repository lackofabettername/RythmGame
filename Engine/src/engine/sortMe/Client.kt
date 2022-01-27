package engine.sortMe

import engine.application.RenderLogic
import engine.application.events.InputEvent
import engine.console.logging.Log
import engine.network.common.NetAddress
import engine.network.common.NetManager
import engine.network.common.NetMessage
import engine.network.common.NetPacket

class Client(
    private val _network: NetManager,
    private val _logic: ClientGameLogic,
    renderLogic: RenderLogic
) {

    init {
        Log.info("Client", "Initializing...")
        _logic.initialize(ClientInfo(this, renderLogic))
    }

    fun updateFrame(deltaTime: Long) {
        _logic.updateFrame(deltaTime)
    }

    fun shutdown() {
        Log.info("Client", "Shutting down...")
        Log.Indent++
        _logic.close()
        Log.Indent--
        Log.info("Client", "Shutdown complete.")
    }

    fun sendMessage(address: NetAddress, message: NetMessage) {
        _network.sendPacket(
            NetPacket(
                address,
                NetAddress.loopbackClient,
                message,
                0,
                false
            )
        )
    }

    //region System event handlers

    fun onInputEvent(info: InputEvent) {
        //TODO: Pass the event to an input handler used by the game simulation?
    }

    fun onNetPacketReceived(packet: NetPacket) {
        Log.trace("Client", "Received $packet")

        if (true) {
            _logic.MessageReceive(packet.Message)
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