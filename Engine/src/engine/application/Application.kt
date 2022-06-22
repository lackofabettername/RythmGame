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
    logic: RenderLogic,
    private val _engine: Engine
) {

    //TODO:
    // - Change to explicit setter for disambiguation?
    // - split into two methods?
    //   - initRenderLogic(), to only init the Renderlogic but not use it
    //   - startRenderLogic(), to start using an initialized RenderLogic
    var Logic = logic
    set(value) {
        Log.info("Application", "Initializing render logic...")
        Log.Indent++
        value.initialize(Window)
        Log.Indent--
        Log.info("Application", "Initialized render logic.")

        field = value

        if (isRunning) {
            start() //TODO: Refactor this?
        }
    }

    private val _inputEventQueue = ArrayBlockingQueue<InputEvent>(QueueCapacity, true)
    val Window = Window("Freehand")

    var isRunning = false
        private set

    //region Core behavior methods
    @Suppress("NAME_SHADOWING")
    fun initialize() {
        Log.info("Application", "Initializing window...")
        Log.Indent++

        Console.registerCVarIfAbsent("app_Width", 900)
        Console.registerCVarIfAbsent("app_Height", 600)
        Console.registerCVarIfAbsent("app_Resizable", false)

        val vSync = Console.getCVar("sys_VSync")!!
        val windW = Console.getCVar("app_Width")!!
        val windH = Console.getCVar("app_Height")!!
        val resizable = Console.getCVar("app_Resizable")!!

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

        //Trigger update
        Logic = Logic
    }

    fun start() {
        Log.info("Application", "Starting render logic...")
        Log.Indent++
        Logic.onStart(_engine)
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
                        Window.Height - y.toFloat()
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
        Logic.onUpdate()
    }

    fun render() {
        if (!Window.ShouldClose) {
            if (Window.IsResized) {//TODO: Unbind any framebuffers?
                Window.updateViewport()
            }

            Logic.onRender()
            Window.swapBuffers()

            Window.IsResized = false
        } else {
            close()
        }
    }

    fun close() {
        if (isRunning) {
            Logic.onClose()
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
        Logic.onInputEvent(event)
    }
    //endregion

    companion object {
        const val QueueCapacity = 512
    }
}