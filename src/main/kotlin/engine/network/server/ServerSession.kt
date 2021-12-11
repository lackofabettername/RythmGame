package engine.network.server

import java.util.HashMap
import engine.network.common.NetAddress

/** Persistent server data between sessions  */
class ServerSession {

    //public NetChallenge[] Challenges;

    private val _connected = HashMap<NetAddress, ServerClient>()

    fun getClient(address: NetAddress): ServerClient? {
        return _connected[address]
    }
}