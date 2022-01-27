package misccelaneous

import engine.console.logging.Log
import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import engine.network.server.ServerClient
import engine.network.server.ServerGameLogic
import engine.network.server.ServerInformation
import util.Vector2

class DummyServerLogic : ServerGameLogic {
    lateinit var server: ServerInformation

    val playerPos = Vector2(0.5f, 0.5f)
    val playerVel = Vector2(0.003f, 0.002f)

    override fun initialize(server: ServerInformation) {
        this.server = server
    }

    override fun update(updateTimeStep: Int) {
        playerPos += playerVel

        if (playerPos.x !in 0f..1f)
            playerVel.x *= -1

        if (playerPos.y !in 0f..1f)
            playerVel.y *= -1

        val client = server.Session.getClient(NetAddress.loopbackClient)
        if (client != null)
            server.sendMessage(client, NetMessage(NetMessageType.SV_GameState, playerPos))
    }

    override fun shutdown() {
    }

    override fun clientMessageReceive(client: ServerClient, message: NetMessage) {
        Log.debug("ServerLogic", "Received $message from $client")
    }
}