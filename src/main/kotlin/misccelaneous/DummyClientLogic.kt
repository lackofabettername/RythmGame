package misccelaneous

import engine.console.logging.Log
import engine.network.common.NetMessage
import engine.network.client.ClientGameLogic
import engine.network.client.ClientInfo
import util.Vector2

class DummyClientLogic : ClientGameLogic {
    lateinit var clientInfo: ClientInfo
    lateinit var renderLogic: DummyRenderLogic
    val playerPos = Vector2()

    override fun initialize(clientInfo: ClientInfo) {
        this.clientInfo = clientInfo
        renderLogic = clientInfo.RenderLogic as DummyRenderLogic
    }

    override fun updateFrame(deltaTime: Long) {
    }

    override fun close() {
    }

    override fun MessageReceive(message: NetMessage) {
        Log.debug("Client Logic", "received $message")

        playerPos copyFrom message.Data as Vector2
        //logic manipulating playerPos
        renderLogic.playerPos copyFrom playerPos
    }
}