package game

import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import imgui.ImGui
import imgui.type.ImString
import logging.Log

class debugGUI(
    val parent: DummyRenderLogic
) : GUIWindow("debug") {
    var txfMessage = ImString()

    override fun render() {
        ImGui.columns(2)

        ImGui.text("Send")
        ImGui.inputTextMultiline("Message", txfMessage)
        if (ImGui.button("Send")) {
            Log.debug("Window", "Sending message ${txfMessage.get()}")
            parent.client.clientInfo.send(
                NetAddress.loopbackServer, NetMessage(
                    NetMessageType.CL_CommandString,
                    txfMessage.get()
                )
            )
        }

        ImGui.nextColumn()

        ImGui.text("Receive")
        ImGui.textWrapped("bla bla bla\nhello world")
    }
}