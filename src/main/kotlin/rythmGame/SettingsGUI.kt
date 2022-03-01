package rythmGame

import engine.console.Console
import engine.console.logging.Log
import imgui.ImGui.*
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImInt
import imgui.type.ImString
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

class SettingsGUI(
    val gui: GUI
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
    }

    override fun render() {
        if (button("Display Demo")) {
            gui.displayDemo = !gui.displayDemo
        }

        newLine()

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

        combo(
            "songs",
            selected,
            musicPaths.map { it.toPath().relativeTo(Path(musicFolderCVar.Text)).toString() }.toTypedArray()
        )
    }
}