package rythmGame

import engine.application.Window
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.glfwGetCurrentContext
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent

class GUI() {
    val Windows = ArrayList<GUIWindow>()

    private lateinit var _parent: Window
    private val _imGuiGlfw = ImGuiImplGlfw()
    private val _imGuiGl3 = ImGuiImplGl3()

    var displayDemo = false

    fun initialize(window: Window) {
        _parent = window
        initializeImGUI()
        _imGuiGlfw.init(_parent.Handle, true)
        _imGuiGl3.init("#version ${Window.GLMajorVersion}${Window.GLMinorVersion}0")
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
        //glfwDestroyWindow(_parent.Handle)
        //glfwTerminate()
    }

    fun render() {
        _imGuiGlfw.newFrame()
        ImGui.newFrame()

        for (window in Windows)
            window.renderProper()

        if (displayDemo) ImGui.showDemoWindow()

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
}

