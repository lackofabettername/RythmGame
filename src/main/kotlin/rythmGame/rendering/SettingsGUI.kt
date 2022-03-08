package rythmGame.rendering

import engine.Engine
import engine.console.Console
import engine.console.logging.Log
import engine.network.client.ClientState
import engine.network.common.NetAddress
import imgui.ImGui.*
import imgui.flag.ImGuiCol
import imgui.type.ImInt
import imgui.type.ImString
import rythmGame.simulation.ClientLogic
import rythmGame.simulation.ServerLogic
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

class SettingsGUI(
    val gui: GUI,
    val parent: MainRenderLogic,
    val engine: Engine,
) : GUIWindow("Settings") {

    init {
        Console.registerCVarIfAbsent("Game_MusicFolder", "resources/music")
    }

    val musicFolderCVar = Console.getCVar("Game_MusicFolder")!!
    val musicFolder = ImString()

    var musicPaths: Array<File> = emptyArray()
    val selected = ImInt()

    init {
        musicFolder.set(musicFolderCVar.Text.removePrefix("src/main/"))
        musicFolderCVar.Dirty = true

        styleColorsClassic()
    }

    override fun render() {
        if (button("Display Demo")) {
            gui.displayDemo = !gui.displayDemo
        }

        newLine()

        if (treeNode("Server")) {
            if (button("Start")) {
                engine.ServerLogic = ServerLogic()
            }

            treePop()
        }

        if (treeNode("Client")) {
            val connected = parent.client != null && (
                    parent.client!!.client.State == ClientState.Connected ||
                            parent.client!!.client.State == ClientState.Active)

            beginDisabled(connected)
            if (button("Connect")) {
                val clientLogic = ClientLogic()
                engine.ClientLogic = clientLogic
                parent.client = clientLogic

                Log.debug("Client", "Connecting to server")
                clientLogic.client.connect(NetAddress.loopbackServer)
            }
            endDisabled()

            treePop()
        }

        if (treeNode("Music")) {
            musicSelection()

            treePop()
        }

        beginDisabled(engine.ClientLogic == null || parent.client!!.client.State != ClientState.Active)
        if (button("Start Game!")) {
            parent.engine.RenderLogic = parent.gameRenderer
        }
        endDisabled()
    }

    fun musicSelection() {
        //region MusicPath
        if (musicPaths.isEmpty())
            pushStyleColor(ImGuiCol.Text, 255f, 0f, 0f, 255f)
        else
            pushStyleColor(ImGuiCol.Text, 255f, 255f, 255f, 255f)

        if (inputText("##MusicFolder", musicFolder)) {
            musicFolderCVar.Text = when {
                musicFolder.get().matches(Regex("^[A-Z]:")) -> musicFolder.get()
                musicFolder.get().startsWith("src/main/") -> musicFolder.get()
                else -> "src/main/${musicFolder.get()}"
            }
        }
        popStyleColor()

        sameLine()
        text("MusicFolder")
        //endregion

        //region Song alternatives
        if (musicFolderCVar.Dirty) {
            musicFolderCVar.clean()
            val file = File(musicFolderCVar.Text)

            musicPaths = emptyArray()
            if (file.exists()) {
                val files = file.listFiles { file -> file.extension == "wav" }
                if (files.isNotEmpty())
                    musicPaths = files
            }
        }

        if (combo(
                "songs",
                selected,
                musicPaths.map { it.toPath().relativeTo(Path(musicFolderCVar.Text)).toString() }.toTypedArray()
            )
        ) {
            //parent.client.client.send(
            //    NetMessage(
            //        NetMessageType.CL_UserCommand,
            //        ClientCommand(
            //            ClientCommand.Type.SongSelection,
            //            musicPaths[selected.get()]
            //        )
            //    )
            //)
            Log.debug("Settings", "Selected ${musicPaths[selected.get()]} (${selected.get()})")
        }
        //endregion
    }
}