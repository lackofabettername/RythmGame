package engine.application

import engine.application.events.*
import engine.console.Console
import logging.Log
import org.lwjgl.glfw.GLFW.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer

//TODO: Variable Canvas size
class Application(
    private val _console: Console,
    private val _logic: WindowCallbacks
) {

    private val _inputEventQueue = ArrayBlockingQueue<InputEvent>(QueueCapacity, true)
    private val _window = Window("Freehand")

    val CanvasW by _window::width
    val CanvasH by _window::height

    var isRunning = false
        private set

    //region Core behavior methods
    @Suppress("NAME_SHADOWING")
    fun initialize() {
        Log.info("Application", "Initializing window...")
        Log.indent++

        _console.registerCVarIfAbsent("app_Width", 900)
        _console.registerCVarIfAbsent("app_Height", 600)
        _console.registerCVarIfAbsent("app_Resizable", false)

        val vSync = _console.getCVar("sys_VSync")!!
        val windW = _console.getCVar("app_Width")!!
        val windH = _console.getCVar("app_Height")!!
        val resizable = _console.getCVar("app_Resizable")!!

        _window.initialize(
            vSync.clean().Flag,
            windW.clean().Value,
            windH.clean().Value,
            resizable.clean().Flag
        )

        vSync.Listeners.add { vSync -> _window.vSync = vSync.clean().Flag }
        windW.Listeners.add { windW -> _window.width = windW.clean().Value }
        windH.Listeners.add { windH -> _window.height = windH.clean().Value }
        resizable.Listeners.add { vSync -> _window.resizable = resizable.clean().Flag }

        setCallbacks()

        Key.initialize()

        Log.indent--
        Log.info("Application", "Initialized window.")

        Log.info("Application", "Initializing render logic...")
        Log.indent++
        _logic.onStart(_window, this::enqueueEvent)
        isRunning = true
        Log.indent--
        Log.info("Application", "Initialized render logic.")
    }

    private fun setCallbacks() {
        with(_window) {
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
        _window.pollEvents()
    }

    fun update() {
        _logic.onUpdate()
    }

    fun render() {
        if (!_window.ShouldClose) {
            _logic.onRender(_window)
            _window.swapBuffers()
        } else {
            close()
        }
    }

    fun close() {
        if (isRunning) {
            _logic.onClose()
            _window.cleanup()
            isRunning = false
        }
    }

    //endregion

    //region Event handlers
    private fun enqueueEvent(event: InputEvent) {
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