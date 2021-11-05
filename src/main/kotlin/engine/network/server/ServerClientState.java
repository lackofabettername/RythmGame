package engine.network.server;

public enum ServerClientState {
    Free,       // Empty slot for a new connection
    Zombie,     // Client has been disconnected, but don't reuse connection for a few seconds
    Connected,  // Client has been assigned to a ServerClient, but does not have a gamestate yet
    Primed,     // Gamestate has been sent, but Client hasn't sent a UserCommand
    Active      // Client is fully in the game
}
