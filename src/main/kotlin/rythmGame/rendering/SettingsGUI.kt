package rythmGame.rendering

import engine.console.Console
import engine.console.logging.Log
import engine.network.common.NetAddress
import engine.network.common.NetMessage
import engine.network.common.NetMessageType
import imgui.ImGui.*
import imgui.flag.ImGuiCol
import imgui.type.ImInt
import imgui.type.ImString
import rythmGame.simulation.ClientCommand
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

class SettingsGUI(
    val gui: GUI,
    val parent: MainRenderLogic
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

        if (treeNode("Music")) {
            musicSelection()

            treePop()
        }

        if (treeNode("Server")) {

            treePop()
        }

        if (treeNode("Client")) {

            treePop()
        }

        if (button("Start Game!")) {
            parent.engine.Application!!.Logic = parent.gameRenderer
        }
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
            parent.client.client.send(
                NetMessage(
                    NetMessageType.CL_UserCommand,
                    ClientCommand(
                        ClientCommand.Type.SongSelection,
                        musicPaths[selected.get()]
                    )
                )
            )
            Log.debug("Settings", "Selected ${musicPaths[selected.get()]} (${selected.get()})")
        }
        //endregion
    }
}