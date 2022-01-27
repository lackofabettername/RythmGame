package engine.network.common

import java.io.Serializable

data class NetMessage(val Type: NetMessageType, val Data: Serializable) : Serializable