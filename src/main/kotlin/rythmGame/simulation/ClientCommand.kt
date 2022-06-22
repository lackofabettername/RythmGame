package rythmGame.simulation

import java.io.Serializable

data class ClientCommand(
    val type: ClientCommandType,
    val data: Serializable
) : Serializable {

}
