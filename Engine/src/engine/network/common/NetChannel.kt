package engine.network.common

/** Used to handle out-of-order packets and duplicate suppression  */
class NetChannel( //TODO
    internal val TargetAddress: NetAddress,
    internal val SenderAddress: NetAddress
) {

    private var _incomingSeq = 0
    private var _outgoingSeq = 0

    fun process(message: NetPacket): Boolean { //TODO
        return true
    }

    fun getPacket(message: NetMessage): NetPacket {
        return NetPacket(TargetAddress, SenderAddress, message, _outgoingSeq++, false)
    }
}