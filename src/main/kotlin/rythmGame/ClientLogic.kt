package rythmGame

import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import engine.sortMe.ClientGameLogic
import engine.sortMe.ClientInfo

class ClientLogic : ClientGameLogic {

    lateinit var clientInfo: ClientInfo

    val player = Player()

    fun playerInput(input: PlayerInput, activate: Boolean) {
        clientInfo.send(
            NetAddress.loopbackServer,
            NetMessage(
                NetMessageType.CL_UserCommand,
                input to activate
            )
        )

        if (activate)
            player.inputs += input
        else
            player.inputs -= input
    }

    override fun initialize(clientInfo: ClientInfo) {
        this.clientInfo = clientInfo
    }

    override fun updateFrame(deltaTime: Long) {
        player.update()
    }

    override fun close() {

    }

    override fun MessageReceive(message: NetMessage) {

    }
}