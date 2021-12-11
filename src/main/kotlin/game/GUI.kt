package game

import engine.application.Window
import imgui.ImGui
import imgui.ImGuiIO
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*

class GUI(
    private var _windows: Array<GUIWindow> = arrayOf()
    ) {

    private lateinit var _parent: Window
    private val _imGuiGlfw = ImGuiImplGlfw()
    private val _imGuiGl3 = ImGuiImplGl3()

    fun initialize(window: Window) {
        _parent = window
        initializeImGUI()
        _imGuiGlfw.init(_parent.Handle, true)
        _imGuiGl3.init("#version 330")
    }

    private fun initializeImGUI() {
        ImGui.createContext()
        val io = ImGui.getIO()
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
    }

    fun cleanup() {
        _imGuiGl3.dispose()
        _imGuiGlfw.dispose()
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(_parent.Handle)
        glfwDestroyWindow(_parent.Handle)
        glfwTerminate()
    }

    fun render() {
        _imGuiGlfw.newFrame()
        ImGui.newFrame()

        for (window in _windows)
            window.renderProper()

        ImGui.showDemoWindow()

        ImGui.render()
        _imGuiGl3.renderDrawData(ImGui.getDrawData())
        // ??? dafuck is this ???
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            glfwMakeContextCurrent(backupWindowPtr)
        }
    }

    fun addWindow(window: GUIWindow) {
        _windows += window
    }
}

