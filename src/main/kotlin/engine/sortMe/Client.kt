package engine.sortMe

import engine.application.events.InputEvent
import engine.network.NetManager
import engine.network.NetPacket
import logging.Log

class Client(
    private val _network: NetManager,
    private val _logic: ClientGameLogic
) {

    fun initialize() {
        _logic.initialize()
    }

    fun updateFrame(deltaTime: Long) {
        _logic.updateFrame(deltaTime)
    }

    fun shutdown() {
        Log.info("Client", "Shutting down...")
        Log.indent++
        _logic.close()
        Log.indent--
        Log.info("Client", "Shutdown complete.")
    }

    //region System event handlers

    fun onInputEvent(info: InputEvent) {
        //TODO: Pass the event to an input handler used by the game simulation?
    }

    fun onNetPacketReceived(packet: NetPacket) {

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