package engine.network.server

import engine.network.NetAddress
import engine.network.NetChannel

class ServerClient(
    address: NetAddress
    ) {
    val Channel = NetChannel(address)
    val Address by Channel::Address
}