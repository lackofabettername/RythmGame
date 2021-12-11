package engine.network.common

import java.util.ArrayList

/** Used to handle out-of-order packets and duplicate suppression  */
class NetChannel(
    internal val TargetAddress: NetAddress,
    internal val SenderAddress: NetAddress
) {

    private var _incomingSeq = 0
    private var _outgoingSeq = 0
    private val _unConfirmedPackets = ArrayList<NetPacket>()

    fun process(message: NetPacket): Boolean {
        if (_unConfirmedPackets.contains(message)) {
            _unConfirmedPackets.remove(message)
        }
        return false
    }

    fun send(message: NetMessage): NetPacket {
        return NetPacket(TargetAddress, SenderAddress, message, _outgoingSeq++, false)
    }

    fun sendReliable(message: NetMessage): NetPacket {
        val packet = NetPacket(TargetAddress, SenderAddress, message, _outgoingSeq++, true)
        _unConfirmedPackets.add(packet)
        return packet
    }
}