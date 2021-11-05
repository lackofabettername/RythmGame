package engine.network.server;

import engine.network.NetMessage;

public class ServerParse {

    public static void clientMessage(ServerClient client, NetMessage message) {

        //TODO: Function pointer mapping? Could be useful for modding.

        switch (message.getType()) {

            case COM_Invalid -> { } //TODO: Drop the client?

            case COM_NoOperation, COM_EndOfFile -> { } // Do nothing...

            case COM_Composite -> {
                NetMessage[] composite = (NetMessage[]) message.getData();
                for (NetMessage next : composite) {
                    clientMessageActual(client, next);
                }
            }

            default -> clientMessageActual(client, message);
        }
    }

    private static void clientMessageActual(ServerClient client, NetMessage message) {
        //TODO: Make sure the command is from the same session.
        switch (message.getType()) {
            case CL_CommandString -> commandString (client, (String)      message.getData());
            //case CL_UserCommand ->   userCommand   (client, (UserCommand) message.getData());
        }
    }

    private static void commandString(ServerClient client, String command) {
        //TODO: Make sure this command has not already been processed.
    }

    //private static void userCommand(ServerClient client, UserCommand command) {
    //    //TODO: Simulate the game!
    //}
}
