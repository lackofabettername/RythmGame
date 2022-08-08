package engine.application

import data.Color
import engine.console.logging.Log
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C
import org.lwjgl.system.MemoryUtil
import java.io.*

class Window(
    val title: String,
) {
    var Handle = 0L; private set
    var Initialized = false; private set

    var Resizable = true
    var IsResized = false

    var Width = 0
        set(value) {
            if (!Resizable) return
            field = value
            if (!Initialized) return
            glfwSetWindowSize(Handle, Width, Height)
        }

    var Height = 0
        set(value) {
            if (!Resizable) return
            field = value
            if (!Initialized) return
            glfwSetWindowSize(Handle, Width, Height)
        }

    val AspectRatio get() = Width.toFloat() / Height.toFloat()

    var VSync = false
        set(value) {
            field = value
            glfwSwapInterval(if (value) 1 else 0)
        }

    fun initialize(vSync: Boolean, width: Int, height: Int, resizable: Boolean) {

        // Create error callback
        run {
            val tempIn = PipedInputStream()
            val tempOut = PipedOutputStream(tempIn)

            val errorThread = object : Thread("GLFW error pipe") {
                override fun run() {
                    val stream = DataInputStream(tempIn)
                    while (!isInterrupted) {
                        try {
                            Log.error("Engine?", stream.readUTF())
                        } catch (e: IOException) {
                            //TODO: close the engine?
                            Log.error("Engine?", e)
                        }
                    }
                }
            }
            errorThread.isDaemon = true //notice: is this dangerous?
            errorThread.start()

            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            GLFWErrorCallback.createPrint(PrintStream(tempOut))//todo: do this without a second thread?
        }

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        this.Width = width;
        this.Height = height;
        this.Resizable = resizable

        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, GLMajorVersion)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, GLMinorVersion)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        glfwWindowHint(GLFW_RESIZABLE, if (Resizable) GL_TRUE else GL_FALSE)

        // Create the window
        Handle = glfwCreateWindow(Width, Height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        check(Handle != MemoryUtil.NULL) { "Failed to create the GLFW window" }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(Handle) { _, width: Int, height: Int ->
            this.Width = width
            this.Height = height
            IsResized = true
        }

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        vidmode?.let {
            // Center our window
            glfwSetWindowPos(
                Handle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
            )
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(Handle)

        //DO NOT MOVE!
        this.VSync = vSync

        // Make the window visible
        glfwShowWindow(Handle)
        GL.createCapabilities()

        // Set the clear color
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        //glEnable(GL_DEPTH_TEST)
        //glEnable(GL_CULL_FACE)
        //glEnable(GL_PROGRAM_POINT_SIZE)
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        //glCullFace(GL_BACK)
    }

    //https://youtrack.jetbrains.com/issue/KT-6519/Setter-only-properties#focus=Comments-27-3525647.0-0
    var clearColor: Color
        @Deprecated("", level = DeprecationLevel.HIDDEN) // Prevent Kotlin callers
        get() = throw UnsupportedOperationException()
        set(color) {
            glClearColor(color.red, color.green, color.blue, color.alpha)
        }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun clear(mask: Int) {
        glClear(mask)
    }

    val ShouldClose: Boolean
        get() = glfwWindowShouldClose(Handle)

    var DepthTest: Boolean
        get() = glGetBoolean(GL_DEPTH_TEST)
        set(state: Boolean) {
            if (state)
                glEnable(GL_DEPTH_TEST)
            else
                glDisable(GL_DEPTH_TEST)
        }

    var CullFace: Boolean
        get() = glGetBoolean(GL_CULL_FACE)
        set(state: Boolean) {
            if (state)
                glEnable(GL_CULL_FACE)
            else
                glDisable(GL_CULL_FACE)
        }

    var Blend: Boolean
        get() = glGetBoolean(GL_BLEND)
        set(state: Boolean) {
            if (state)
                glEnable(GL_BLEND)
            else
                glDisable(GL_BLEND)
        }

    fun updateViewport(x: Int = 0, y: Int = 0, w: Int = Width, h: Int = Height) {
        glViewport(x, y, w, h)
    }

    //<editor-fold desc="Callbacks ">
    fun setKeyCallback(callback: GLFWKeyCallbackI) {
        glfwSetKeyCallback(Handle, callback)
    }

    fun setCursorPosCallback(callback: GLFWCursorPosCallbackI) {
        glfwSetCursorPosCallback(Handle, callback)
    }

    fun setCursorEnterCallback(callback: GLFWCursorEnterCallbackI) {
        glfwSetCursorEnterCallback(Handle, callback)
    }

    fun setMouseButtonCallback(callback: GLFWMouseButtonCallbackI) {
        glfwSetMouseButtonCallback(Handle, callback)
    }

    fun setScrollCallback(callback: GLFWScrollCallbackI) {
        glfwSetScrollCallback(Handle, callback)
    }
    //</editor-fold>

    fun pollEvents() {
        glfwPollEvents()
    }

    fun swapBuffers() {
        glfwSwapBuffers(Handle)
    }

    fun cleanup() {
        glfwDestroyWindow(Handle)
        glfwTerminate()
    }

    companion object {
        fun bind() {
            GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0)
        }

        val GLMajorVersion = 4
        val GLMinorVersion = 6

        val ColorBuffer = GL_COLOR_BUFFER_BIT
        val DepthBuffer = GL_DEPTH_BUFFER_BIT
        val StencilBuffer = GL_STENCIL_BUFFER_BIT
    }
}