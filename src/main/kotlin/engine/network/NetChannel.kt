package engine.network

import java.util.ArrayList

/** Used to handle out-of-order packets and duplicate suppression  */
class NetChannel(
    internal val Address: NetAddress
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

    fun send(message: NetMessage?): NetPacket {
        return NetPacket(Address, message!!, _outgoingSeq++, false)
    }

    fun sendReliable(message: NetMessage?): NetPacket {
        val packet = NetPacket(Address, message!!, _outgoingSeq++, true)
        _unConfirmedPackets.add(packet)
        return packet
    }
}