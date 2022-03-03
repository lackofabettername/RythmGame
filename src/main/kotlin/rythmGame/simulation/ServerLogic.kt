package rythmGame.simulation

import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import engine.network.server.ServerClient
import engine.network.server.ServerGameLogic
import engine.network.server.ServerInformation
import rythmGame.common.GameState
import rythmGame.common.PlayerInput

class ServerLogic : ServerGameLogic {

    lateinit var server: ServerInformation

    val gameState = GameState()

    override fun initialize(server: ServerInformation) {
        this.server = server
    }

    override fun update(updateTimeStep: Int) {
        with(gameState) {
            timeStamp += updateTimeStep

            player.update(updateTimeStep / 100f)
        }

        server.Session.getClient(NetAddress.loopbackClient)?.let { client ->
            server.sendMessage(
                client,
                NetMessage(NetMessageType.SV_GameState, gameState)
            )
        }

    }

    override fun shutdown() {
    }

    @Suppress("UNCHECKED_CAST")
    override fun clientMessageReceive(client: ServerClient, message: NetMessage) {
        when (message.Type) {
            NetMessageType.CL_UserCommand -> clientInput(message.Data as Pair<PlayerInput, Boolean>)
        }
        //Log.debug("ServerLogic", "Received $message from $client")
    }

    fun clientInput(input: Pair<PlayerInput, Boolean>) {
        val (input, activated) = input
        if (activated)
            gameState.player.inputs += input
        else
            gameState.player.inputs -= input
    }
}