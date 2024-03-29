package engine.network.server

import engine.network.common.NetAddress

/** Persistent server data between sessions  */
class ServerSession {

    //public NetChallenge[] Challenges;

    private val _connected = HashMap<NetAddress, ServerClient>()
    val Clients get() = _connected.values

    fun init() {
        _connected.clear()
    }

    fun getClient(address: NetAddress): ServerClient? {
        return _connected[address]
    }

    fun addClient(address: NetAddress, client: ServerClient) {
        _connected[address] = client
    }
}