package engine.network

enum class NetMessageType {

    // Shared protocol commands
    COM_Invalid,                    // null
    COM_NoOperation,                // NetCommand.Null
    COM_EndOfFile,                  // NetCommand.Null
    COM_Composite,                  // NetCommand[]

    // Executed by the client
    SV_CommandString,               // String
    SV_GameState,                   // GameState
    SV_GameStateDelta,              // GameStateDelta
    SV_Download,                    // NetFile?

    // Executed by the server
    CL_CommandString,               // String
    CL_UserCommand                  // UserCommand
}