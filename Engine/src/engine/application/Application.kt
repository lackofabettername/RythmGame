package engine.application

import engine.Engine
import engine.application.events.*
import engine.console.Console
import engine.console.logging.Log
import org.lwjgl.glfw.GLFW.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer

//TODO: Variable Canvas size
class Application(
    private val _console: Console,
    private val _logic: RenderLogic,
    private val _engine: Engine
) {

    private val _inputEventQueue = ArrayBlockingQueue<InputEvent>(QueueCapacity, true)
    val Window = Window("Freehand")

    var isRunning = false
        private set

    //region Core behavior methods
    @Suppress("NAME_SHADOWING")
    fun initialize() {
        Log.info("Application", "Initializing window...")
        Log.Indent++

        _console.registerCVarIfAbsent("app_Width", 900)
        _console.registerCVarIfAbsent("app_Height", 600)
        _console.registerCVarIfAbsent("app_Resizable", false)

        val vSync = _console.getCVar("sys_VSync")!!
        val windW = _console.getCVar("app_Width")!!
        val windH = _console.getCVar("app_Height")!!
        val resizable = _console.getCVar("app_Resizable")!!

        Window.initialize(
            vSync.clean().Flag,
            windW.clean().Value,
            windH.clean().Value,
            resizable.clean().Flag
        )

        vSync.Listeners.add { vSync -> Window.VSync = vSync.clean().Flag }
        windW.Listeners.add { windW -> Window.Width = windW.clean().Value }
        windH.Listeners.add { windH -> Window.Height = windH.clean().Value }
        resizable.Listeners.add { _ -> Log.warn("Changing window resizability requires restart") }

        setCallbacks()

        Key.initialize()

        Log.Indent--
        Log.info("Application", "Initialized window.")

        Log.info("Application", "Initializing render logic...")
        Log.Indent++
        _logic.initialize(Window)
        Log.Indent--
        Log.info("Application", "Initialized render logic.")
    }

    fun start() {
        Log.info("Application", "Starting render logic...")
        Log.Indent++
        _logic.onStart(_engine)
        isRunning = true
        Log.Indent--
        Log.info("Application", "Started render logic.")
    }

    private fun setCallbacks() {
        with(Window) {
            setKeyCallback { window, key, scancode, action, mods ->
                enqueueEvent(
                    KeyEvent(
                        when (action) {
                            GLFW_PRESS -> KeyEventType.Pressed
                            GLFW_REPEAT -> KeyEventType.Repeat
                            GLFW_RELEASE -> KeyEventType.Released
                            else -> KeyEventType.Unknown
                        },
                        Key(key, scancode, Modifiers(mods))
                    )
                )
            }

            setCursorPosCallback { window, x, y ->
                enqueueEvent(
                    MouseEvent(
                        MouseEventType.Moved,
                        x.toFloat(),
                        y.toFloat()
                    )
                )
            }

            setCursorEnterCallback { window, entered ->
                enqueueEvent(
                    MouseEvent(
                        MouseEventType.Enter,
                        entered
                    )
                )
            }

            setMouseButtonCallback { window, button, action, modifiers ->
                enqueueEvent(
                    MouseEvent(
                        when (action) {
                            GLFW_PRESS -> MouseEventType.ButtonPressed
                            GLFW_RELEASE -> MouseEventType.ButtonReleased
                            else -> MouseEventType.Unkown
                        },
                        button,
                        modifiers
                    )
                )
            }

            setScrollCallback { window, x, y ->
                enqueueEvent(
                    MouseEvent(
                        MouseEventType.Wheel,
                        x.toFloat(),
                        y.toFloat()
                    )
                )
            }
        }
    }

    fun pollEvents() {
        Window.pollEvents()
    }

    fun update() {
        _logic.onUpdate()
    }

    fun render() {
        if (!Window.ShouldClose) {
            if (Window.IsResized) {//TODO: Unbind any framebuffers?
                Window.updateViewport()
            }

            _logic.onRender()
            Window.swapBuffers()

            Window.IsResized = false
        } else {
            close()
        }
    }

    fun close() {
        if (isRunning) {
            _logic.onClose()
            Window.cleanup()
            isRunning = false
        }
    }

    //endregion

    //region Event handlers
    fun enqueueEvent(event: InputEvent) {
        if (_inputEventQueue.remainingCapacity() == 0) {
            Log.warn("Application", "Input queue overflow, discarding oldest event!")
            _inputEventQueue.remove()
        }
        _inputEventQueue.offer(event)
    }

    fun rerouteInputEvents(callback: Consumer<InputEvent>) {
        while (!_inputEventQueue.isEmpty()) {
            callback.accept(_inputEventQueue.poll())
        }
    }

    fun onInputEvent(event: InputEvent) {
        _logic.onInputEvent(event)
    }
    //endregion

    companion object {
        const val QueueCapacity = 512
    }
}