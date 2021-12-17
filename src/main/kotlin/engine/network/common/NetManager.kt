package engine.network.common

import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer

class NetManager {
    //TODO: Send messages from outgoing message queue.
    private var _incomingMessages = ArrayBlockingQueue<NetPacket>(QueueCapacity, true)
    private var _outgoingMessages = ArrayBlockingQueue<NetPacket>(QueueCapacity, true)
    private var _loopbackToServer = NetLoopback()
    private var _loopbackToClient = NetLoopback()

    @Volatile
    private var _isOpen = true

    fun close() {
        _isOpen = false
    }

    fun sendPacket(packet: NetPacket): Boolean {
        return _isOpen && when (packet.TargetAddress.AddressType) {
            NetAddressType.Internet -> _outgoingMessages.offer(packet)
            NetAddressType.Loopback -> when (packet.TargetAddress.Addressable) {
                NetAddressable.Unknown -> false
                NetAddressable.Server -> _loopbackToServer.sendPacket(packet)
                NetAddressable.Client -> _loopbackToClient.sendPacket(packet)
            }
            else -> false
        }
    }

    //region Rerouting inbound packets
    fun consumeInternetPackets(callback: Consumer<NetPacket>) {
        while (!_incomingMessages.isEmpty()) {
            callback.accept(_incomingMessages.remove())
        }
    }

    fun consumeLoopbackPackets(callback: Consumer<NetPacket>, target: NetAddressable) {

        val loopback = when (target) {
            NetAddressable.Unknown -> return
            NetAddressable.Server -> _loopbackToServer
            NetAddressable.Client -> _loopbackToClient
        }

        while (!loopback.isEmpty) {
            callback.accept(loopback.getMessage())
        }
    }
    //endregion

    companion object {
        const val QueueCapacity = 1024
    }
}