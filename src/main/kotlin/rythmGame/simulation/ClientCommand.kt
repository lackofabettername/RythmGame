package rythmGame.simulation

import java.io.Serializable

data class ClientCommand(
    val type: Type,
    val data: Serializable
) : Serializable {
    enum class Type {
        PlayerMovement,
        SongSelection
    }
}
