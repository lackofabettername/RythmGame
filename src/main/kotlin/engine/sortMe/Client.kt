package engine.sortMe

import engine.application.events.InputEvent
import engine.network.common.NetAddress
import engine.network.common.NetManager
import engine.network.common.NetMessage
import engine.network.common.NetPacket
import logging.Log

class Client(
    private val _network: NetManager,
    private val _logic: ClientGameLogic
) {

    fun initialize() {
        Log.info("Client", "Initializing...")
        _logic.initialize(this::sendMessage)
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

    private fun sendMessage(address: NetAddress, message: NetMessage) {
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