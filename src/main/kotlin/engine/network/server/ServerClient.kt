package engine.network.server

import engine.network.common.NetAddress
import engine.network.common.NetChannel

class ServerClient(
    targetAddress: NetAddress,
    senderAddress: NetAddress,
    ) {
    val Channel = NetChannel(targetAddress, senderAddress)
    val Address by Channel::TargetAddress
}