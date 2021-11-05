package engine.application

import logging.Log
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE
import org.lwjgl.system.MemoryUtil
import java.io.*

class Window(
    val title: String,
    width: Int,
    height: Int,
    vSync: Boolean,
) {

    var height = height; private set
    var width = width; private set
    val AspectRatio get() = width.toFloat() / height.toFloat()

    var vSync = vSync
        set(value) {
            field = value
            GLFW.glfwSwapInterval(if (value) 1 else 0)
        }

    var Handle: Long = 0; private set

    var isResized = false
    var reziable = false
        set(value) {
            field = value
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if (value) GL_TRUE else GL_FALSE)
        }

    fun initialize() {
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

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
        reziable = true
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)

        // Create the window
        Handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        check(Handle != MemoryUtil.NULL) { "Failed to create the GLFW window" }

        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(Handle) { _, width: Int, height: Int ->
            this.width = width
            this.height = height
            isResized = true
        }

        // Get the resolution of the primary monitor
        val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        vidmode?.let {
            // Center our window
            GLFW.glfwSetWindowPos(
                Handle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
            )
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(Handle)

        //DO NOT REMOVE!
        vSync = vSync

        // Make the window visible
        GLFW.glfwShowWindow(Handle)
        GL.createCapabilities()

        // Set the clear color
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glEnable(GL_PROGRAM_POINT_SIZE)
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        //glCullFace(GL_BACK)
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun clear(mask: Int) {
        glClear(mask)
    }

    val ShouldClose: Boolean
        get() = GLFW.glfwWindowShouldClose(Handle)

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

    fun pollEvents() {
        GLFW.glfwPollEvents()
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(Handle)
    }

    fun cleanup() {
        GLFW.glfwTerminate()
    }
}