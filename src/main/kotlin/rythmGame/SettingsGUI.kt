package rythmGame

import engine.console.Console
import imgui.ImGui.*
import imgui.type.ImInt
import imgui.type.ImString
import java.io.File

class SettingsGUI(
    val gui: GUI
) : GUIWindow("Settings") {

    val musicFolderCVar = Console.registerCVarIfAbsent("Game_MusicFolder", "resources/music")
    val musicFolder = ImString()

    var musicPaths: Array<File> = emptyArray()
    val selected = ImInt()

    init {
        musicFolder.set(musicFolderCVar.Text)
        musicFolderCVar.Dirty = true
    }

    override fun render() {
        if (button("Display Demo")) {
            gui.displayDemo = !gui.displayDemo
        }

        newLine()

        if (inputText("MusicFolder", musicFolder)) {
            Console.getCVar("Game_MusicFolder")!!.Text = musicFolder.get()
        }

        if (musicFolderCVar.Dirty) {
            musicFolderCVar.clean()
            val file = File(musicFolderCVar.Text)
            val files = file.listFiles()
            if (files.isNotEmpty())
                musicPaths = files
        }

        combo("songs", selected, musicPaths.map { it.toString() }.toTypedArray())
    }
}