package engine.network.server

import java.util.HashMap
import engine.network.NetAddress
import engine.network.server.ServerClient

/** Persistent server data between sessions  */
class ServerSession {

    //public NetChallenge[] Challenges;

    private val _connected = HashMap<NetAddress, ServerClient>()

    fun getClient(address: NetAddress): ServerClient? {
        return _connected[address]
    }
}