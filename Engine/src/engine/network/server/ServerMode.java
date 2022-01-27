package engine.network.server;

public enum ServerMode {
    Single,     // Offline: single-player mode
    Listen,     // Internet hosting: client hosting server in the same process
    Dedicated   // Internet connection required: running without a client
    //TODO: Dedicated: Hosting other servers in a public browser?
}
