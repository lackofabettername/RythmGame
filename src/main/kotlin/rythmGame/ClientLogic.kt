package rythmGame

import engine.console.logging.Log
import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import engine.sortMe.ClientGameLogic
import engine.sortMe.ClientInfo

class ClientLogic : ClientGameLogic {

    lateinit var client: ClientInfo

    val player = Player()

    fun playerInput(input: PlayerInput, activate: Boolean) {
        client.send(
            NetAddress.loopbackServer,
            NetMessage(
                NetMessageType.CL_UserCommand,
                input to activate
            )
        )
    }

    override fun initialize(clientInfo: ClientInfo) {
        this.client = clientInfo
    }

    override fun updateFrame(deltaTime: Long) {
        player.update()
    }

    override fun close() {

    }

    override fun MessageReceive(message: NetMessage) {
        Log.debug("ClientLogic", "Received $message")

        when (message.Type) {
            NetMessageType.SV_GameState -> gameStateReceived(message.Data as Player)
        }
    }

    fun gameStateReceived(player: Player) {
        this.player.pos copyFrom player.pos
    }
}