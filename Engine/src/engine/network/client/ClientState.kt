package engine.network.client

enum class ClientState {
    /** Client isn't connected and hasn't tried to connect */
    Disconected,

    /** Client has tried to connect but hasn't been accepted yet */
    Waiting,

    /** Client has been accepted by server but does not have a gamestate yet */
    Connected,

    /** Client is fully in the game */
    Active
}