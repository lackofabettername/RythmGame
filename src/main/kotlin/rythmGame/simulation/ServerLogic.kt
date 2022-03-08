package rythmGame.simulation

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

        server.Session.Clients.forEach { client ->
            server.sendMessage(
                client,
                NetMessage(NetMessageType.SV_GameState, gameState)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun clientMessageReceive(client: ServerClient, message: NetMessage) {
        when (message.Type) {
            NetMessageType.CL_UserCommand -> clientInput(message.Data as ClientCommand)
        }
        //Log.debug("ServerLogic", "Received $message from $client")
    }

    override fun clientConnect(client: ServerClient, message: String): Boolean {
        //todo
        return true //Accept all clients
    }

    override fun shutdown() {
    }

    @Suppress("UNCHECKED_CAST")
    fun clientInput(input: ClientCommand) {
        when (input.type) {
            ClientCommand.Type.PlayerMovement -> {
                val (input, activated) = input.data as Pair<PlayerInput, Boolean>
                if (activated)
                    gameState.player.inputs += input
                else
                    gameState.player.inputs -= input
            }
        }
    }
}