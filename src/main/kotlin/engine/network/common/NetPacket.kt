package engine.network.common

import java.io.Serializable

data class NetPacket @JvmOverloads constructor(//TODO: Identity for both sender and receiver, switch on receive...
    val TargetAddress: NetAddress,
    val SenderAddress: NetAddress,
    val Message: NetMessage,
    val SequenceNumber: Int = -1,
    val IsReliable: Boolean = false
) : Serializable {

    var SendTime = -1L //TODO: Change this?

    companion object {
        const val MaxSize = 1600
    }
}