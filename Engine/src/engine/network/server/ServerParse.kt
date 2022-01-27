package engine.network.server

import engine.network.common.NetMessage
import engine.network.common.NetMessageType.*

object ServerParse {
    fun clientMessage(logic: ServerGameLogic, client: ServerClient, message: NetMessage) {

        //TODO: Function pointer mapping? Could be useful for modding.

        when (message.Type) {

            COM_Invalid -> {
            } //TODO: Drop the client?

            COM_NoOperation, COM_EndOfFile -> {
            } // Do nothing...

            COM_Composite -> {
                val composite = message.Data as Array<NetMessage>;
                for (next in composite) {
                    logic.clientMessageReceive(client, next);
                }
            }

            else -> logic.clientMessageReceive(client, message);
        }
    }
}