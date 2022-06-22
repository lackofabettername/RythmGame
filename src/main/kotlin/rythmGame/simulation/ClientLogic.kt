package rythmGame.simulation

import engine.application.events.MouseEvent
import engine.console.logging.Log
import engine.network.client.ClientGameLogic
import engine.network.client.ClientInfo
import engine.network.client.ClientState
import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import rythmGame.common.GameState
import rythmGame.common.PlayerInput

class ClientLogic : ClientGameLogic {

    lateinit var client: ClientInfo

    val gsFuture = GameState()
    val gsPast = GameState()
    val gsNow = GameState()

    var time = -1L

    fun playerInput(input: PlayerInput, activate: Boolean) {
        client.send(
            NetAddress.loopbackServer,
            NetMessage(
                NetMessageType.CL_UserCommand,
                ClientCommand(
                    ClientCommandType.PlayerMovement,
                    input to activate
                )
            )
        )
    }

    fun playerInput(event: MouseEvent) {
        client.send(
            NetAddress.loopbackServer,
            NetMessage(
                NetMessageType.CL_UserCommand,
                ClientCommand(
                    ClientCommandType.PlayerMouse,
                    event
                )
            )
        )
    }

    override fun initialize(clientInfo: ClientInfo) {
        this.client = clientInfo
    }

    override fun updateFrame(deltaTime: Long) {
        if (client.State != ClientState.Active) return

        time += deltaTime
        val t = (time - gsPast.timeStamp).toFloat() / (gsFuture.timeStamp - gsPast.timeStamp) - 1
        //if (t !in 0f..1f) Log.debug("$t")
        gsNow.lerp(t, gsPast, gsFuture)
    }

    override fun close() {

    }

    override fun MessageReceive(message: NetMessage) {
        Log.trace("ClientLogic", "Received $message")

        when (message.Type) {
            NetMessageType.SV_GameState -> gameStateReceived(message.Data as GameState)
        }
    }

    fun gameStateReceived(gameState: GameState) {
        if (time == -1L) time = gameState.timeStamp

        gsPast copyFrom gsFuture
        gsFuture copyFrom gameState

    }
}