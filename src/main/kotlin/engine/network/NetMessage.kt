package engine.network

import java.io.Serializable

class NetMessage(val Type: NetMessageType, val Data: Serializable) : Serializable