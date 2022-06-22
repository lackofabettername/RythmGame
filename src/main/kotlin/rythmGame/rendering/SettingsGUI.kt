package rythmGame.rendering

import engine.Engine
import engine.console.Console
import engine.console.logging.Log
import engine.console.logging.style.Font
import engine.console.logging.style.Style
import engine.network.client.ClientState
import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import imgui.ImGui.*
import imgui.flag.ImGuiCol
import imgui.type.ImInt
import imgui.type.ImString
import rythmGame.simulation.ClientCommand
import rythmGame.simulation.ClientCommandType
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

    var musicFiles: Array<File> = emptyArray()
    var musicPaths: Array<String> = emptyArray()
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
                startServer()
            }

            treePop()
        }

        if (treeNode("Client")) {
            val connected = parent.client != null && (
                    parent.client!!.client.State == ClientState.Connected ||
                            parent.client!!.client.State == ClientState.Active)

            beginDisabled(connected)
            if (button("Connect")) {
                startClient()
            }
            endDisabled()

            treePop()
        }

        if (treeNode("Music")) {
            musicSelection()

            treePop()
        }

        beginDisabled(engine.ClientLogic == null || parent.client!!.client.State != ClientState.Active)
        if (button("Start Online Game!")) {
            startGame()
        }
        sameLine()
        endDisabled()

        beginDisabled(parent.client?.client != null && parent.client?.client?.State != ClientState.Active)
        if (button("Start Local Game!")) {
            startServer()
            startClient()
            startGame()
        }
        endDisabled()

    }

    fun musicSelection() {
        //region MusicPath
        if (musicFiles.isEmpty())
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
            val musicFolder = File(musicFolderCVar.Text)

            musicFiles = emptyArray()
            if (musicFolder.exists()) {
                val musicFiles = musicFolder.listFiles { file -> file.extension == "wav" }
                if (musicFiles?.isNotEmpty() == true) {
                    this.musicFiles = musicFiles
                    musicPaths = musicFiles.map {
                        it.toPath().relativeTo(Path(musicFolderCVar.Text)).toString()
                    }.toTypedArray()
                }
            }
        }

        if (combo("songs", selected, musicPaths)) {
            Log.debug("Settings", "Sending to ${parent.client!!.client.ServerAddress}")

            parent.client!!.client.send(
                NetMessage(
                    NetMessageType.CL_UserCommand,
                    ClientCommand(
                        ClientCommandType.SongSelection,
                        musicPaths[selected.get()]
                    )
                )
            )

            Log.debug(
                "Settings",
                "Selected \"${Font.Italics}${musicPaths[selected.get()]}${Style.Reset}\" (${selected.get()})"
            )
        }
        //endregion
    }

    private fun startServer() {
        engine.ServerLogic = ServerLogic()
    }

    private fun startClient() {
        val clientLogic = ClientLogic()
        engine.ClientLogic = clientLogic
        parent.client = clientLogic

        Log.debug("Client", "Connecting to server")
        clientLogic.client.connect(NetAddress.loopbackServer)
    }

    private fun startGame() {
        parent.engine.RenderLogic = parent.gameRenderer
    }
}
