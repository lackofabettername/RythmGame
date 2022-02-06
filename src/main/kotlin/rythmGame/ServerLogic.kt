package rythmGame

import engine.console.logging.Log
import engine.network.common.NetMessage
import engine.network.server.ServerClient
import engine.network.server.ServerGameLogic
import engine.network.server.ServerInformation

class ServerLogic : ServerGameLogic {
    override fun initialize(server: ServerInformation) {
    }

    override fun update(updateTimeStep: Int) {
    }

    override fun shutdown() {
    }

    override fun clientMessageReceive(client: ServerClient, message: NetMessage) {
        Log.debug("ServerLogic", "Received $message from $client")
    }
}