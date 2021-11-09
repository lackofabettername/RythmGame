package engine.sortMe

import engine.application.Application
import engine.application.Window
import engine.application.WindowCallbacks
import engine.application.events.*
import engine.console.Console
import engine.console.ConsoleCommand
import engine.events.SysEvent
import engine.events.SysEventManager
import engine.events.SysEventType
import engine.files.FileSystem
import engine.network.NetAddressable
import engine.network.NetManager
import engine.network.server.Server
import logging.Log
import logging.LogLevel
import logging.style.Foreground
import logging.style.Style
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer

class Engine(
    serverGameLogic: ServerGameLogic,
    clientGameLogic: ClientGameLogic,
    renderGameLogic: WindowCallbacks
) {

    //region Main engine modules
    val Console: Console = Console()
    val Window: Application = Application(Console, renderGameLogic)

    val Network = NetManager()
    private val _events = SysEventManager(this)
    private var _server: Server? = Server(Network, serverGameLogic)
    private var _client: Client? = Client(Network, clientGameLogic)
    //endregion

    private val _commandQueue = ArrayBlockingQueue<ConsoleCommand>(512, true)

    private var _isServerDedicated = false
    private var _isRunning = false

    fun run() {
        try {
            initialize()
            mainLoop()
        } catch (e: Throwable) {
            try {
                Log.error("Engine", "A catastrophic error occurred.", e)
            } catch (ignored: Throwable) {
                println("(printing again as the Logger may not be functional)\nA catastrophic error occurred.")
                e.printStackTrace()
            }
        } finally {
            //TODO: Don't call this if the "exit" console command was triggered.
            shutdown()
        }
    }

    private fun initialize() {
        run {
            Locale.setDefault(Locale.ENGLISH) // Stupid Swedish computer uses , instead of . for floats

            val time = DateTimeFormatter.ofPattern("yyyy/mm/dd HH:mm:ss").format(LocalDateTime.now())

            //val md = MessageDigest.getInstance("SHA-1")
            //val root = File("src/main/")
            //getChecksum(root, md)
            //val digest: ByteArray = md.digest()
            //val checksum = digest.toHex()

            //Log.info("Game Engine", "$time | $checksum")
            Log.info("Engine", "$time")
        }

        Log.info("Engine", "Initializing subsystems...")
        Log.indent++

        FileSystem.loadConfiguration(Console)
        Console.registerCVarIfAbsent("sys_MaxRenderFPS", 0) // Uncapped
        Console.registerCVarIfAbsent("sys_VSync", false)

        Console.initialize()
        Window.initialize()
        Network.initialize()
        _events.initialize()
        //_server.initialize()
        _client?.initialize()
        _isRunning = true

        Log.indent--
        Log.info("Engine", "Initialization complete.")
    }

    private fun shutdown() {
        Log.info("Engine", "Shutting down...")
        Log.indent++
        _client?.shutdown()
        //_server.shutdown()
        _events.close()
        Network.close()
        Window.close()
        Console.close()
        FileSystem.close()
        _isRunning = false
        Log.indent--
        Log.info("Engine", "Shutdown complete.")

        Log.info("Log", "Closing log file...")
        Log.close()
    }

    private fun processEventLoop(): Long {

        var event: SysEvent

        do {

            // Get an event from the queue or journal
            event = _events.getEvent()

            if (event.Type != SysEventType.Empty)
                Log.trace("Engine", "Processing event: $event")

            when (event.Type) {
                SysEventType.Empty -> {
                    // Process the loopback channels for local play
                    //Network.consumeLoopbackPackets(_server::onNetPacketReceived, NetAddressable.Server)
                    _client?.let {
                        Network.consumeLoopbackPackets(it::onNetPacketReceived, NetAddressable.Client)
                    }
                }

                SysEventType.ConsoleCommand -> _commandQueue.add(event.Reference as ConsoleCommand) // Executed later

                // Inputs will be processed by the client,
                // the important inputs will be sent to the server for further processing.
                SysEventType.Input -> {
                    val event = event.Reference as InputEvent

                    if (event.EventType == InputEventType.Application)
                        _events.enqueueEvent(
                            SysEvent(
                                SysEventType.ConsoleCommand,
                                0,
                                (event as ApplicationEvent).Command
                            )
                        ) //Does this cause a frame of latency?
                    else
                        Window.onInputEvent(event)

                    // _client.onInputEvent(event.Reference as InputEvent?)
                }

                SysEventType.Packet -> {
                    //    NetPacket message =(NetPacket) event . Reference
                    //            when (message.Address.Addressable) {
                    //                Server -> _server.onNetPacketReceived(message)
                    //                Client -> _client.onNetPacketReceived(message)
                    //            }
                }
            }

        } while (event.Type !== SysEventType.Empty)

        return event.Timestamp // The "current" time...

    }

    private fun executeSystemCommands() {
        //TODO: Map command text strings (defined in a table?) to function pointers.
        while (!_commandQueue.isEmpty()) {

            val command = _commandQueue.remove()
            Log.trace("Engine", "Handling command: ${Foreground.LightMagenta}${command.Text}${Style.Reset}...")

            when (command.Text) {
                "exit" -> _isRunning = false

                //"sv_shutdown" -> _server.shutdown()

                else -> {
                    val cvar = Console.getCVar(command.Text)
                    if (cvar != null) {
                        when (command.Args.size) {
                            0 -> Log.info("Engine", "${cvar.get()}")
                            1 -> cvar.set(command.Args[0])
                            else -> Log.warn("Engine", "Unknown command")
                        }
                    }
                }
            }
        }
    }

    private var _updateCount = 0
    private fun mainLoop() {

        _isRunning = true

        var deltaTime: Long
        var deltaTimeMin: Long
        var eventTime: Long
        var eventTimeLast = System.currentTimeMillis()

        val maxFPS = Console.getCVar("sys_MaxRenderFPS")!!
        val vSync = Console.getCVar("sys_VSync")!!
        deltaTimeMin = if (!_isServerDedicated && maxFPS.Value > 0 && !vSync.Flag) {
            1000L / maxFPS.Value
        } else {
            0L
        }

        while (_isRunning) {
            Log.trace("Engine", "Update #$_updateCount")
            ++_updateCount

            // ---------------------
            //  Core systems update
            // ---------------------
            _events.captureInputs() // Flush the input events once per frame...

            FileSystem.writeConfiguration(Console)
            if (maxFPS.Dirty || vSync.Dirty) {
                if (!_isServerDedicated) {
                    deltaTimeMin = if (maxFPS.Value > 0 && !vSync.Flag) {
                        1000L / maxFPS.Value
                    } else {
                        0 // Uncapped
                    }
                }
                maxFPS.Clean = true
                vSync.Clean = true
            }

            // Spin here if things are going too fast...
            do {
                eventTime = processEventLoop()
                if (eventTimeLast > eventTime) {    // Possible on first frame of journal playback...
                    eventTimeLast = eventTime // Protect against negative delta times.
                }
                deltaTime = eventTime - eventTimeLast
                Thread.onSpinWait() //Reduce system strain.
                //TODO: Add wait() to reduce busy wait? Check delta time so we don't over-wait?
            } while (deltaTime < deltaTimeMin)
            eventTimeLast = eventTime

            executeSystemCommands()

            //TODO: Mess with the delta time here...

            // --------------------
            //  Server-side update
            // --------------------
            //_server.updateFrame(deltaTime)

            // Startup or shutdown the client system?
            //if (_isServerDedicated != _server.Dedicated) {
            //    //TODO: Open or close the console?
            //    _isServerDedicated = _server.Dedicated
            //    if (_isServerDedicated) {
            //        _client.shutdown()
            //    } else {
            //        _client.initialize()
            //    }
            //}

            // --------------------
            // Client-side update
            // --------------------
            if (!_isServerDedicated) {
                processEventLoop() // Run again to avoid a frame of latency...
                executeSystemCommands()
                //_client.updateFrame(deltaTime)

                //TODO: Options for rendering the client-side simulation...
                // - Command the window to render and allow it to notify the client via interface or raw method call.
                // - Command the window to send a notification to a shared (with the client) simulation/game object.
                // - Pass the window to the client and forget about it. Then ask it to render.
                //TODO: Multi-thread the client updating and rendering? Research required.
                if (Window.isRunning) {
                    Window.update()
                    Window.render()
                } else {
                    _isRunning = false
                }
            }
        }
    }
}

fun main() {
    Log.logLevel = LogLevel.Trace

    val serverGameLogic = object : ServerGameLogic {

    }

    val clientGameLogic = object : ClientGameLogic {
        override fun initialize() {

        }

        override fun updateFrame(deltaTime: Long) {

        }

        override fun close() {

        }

    }

    val renderGameLogic = object : WindowCallbacks {
        lateinit var parentLoopback: Consumer<ApplicationEvent>
        var state = false
        var v = 0f

        var frameCount = 0
        override fun onStart(parentLoopback: Consumer<ApplicationEvent>) {
            this.parentLoopback = parentLoopback
        }

        override fun onUpdate() {
        }

        override fun onRender(window: Window) {
            Log.trace("RenderLogic", "frame #$frameCount")
            ++frameCount

            if (state) {
                window.setClearColor(v, 1f, 1f, 1f)
            } else {
                window.setClearColor(v, 0f, 0f, 1f)
            }
            v = (v + 1) % 2

            Log.trace("RenderLogic", "Rendering state $state")
            window.clear(Window.ColorBuffer)
        }

        override fun onClose() {
        }

        override fun onInputEvent(event: InputEvent) {
            Log.trace("RenderLogic", "Handling event $event")
            if (event.EventType == InputEventType.Key) {
                if ((event as KeyEvent).Type == KeyEventType.Pressed) {
                    when (event.Key) {
                        Key.Escape -> {
                            parentLoopback.accept(
                                ApplicationEvent(ConsoleCommand("exit", ""))
                            )
                        }
                        Key.Space -> {
                            state = !state
                            Log.trace("RenderLogic", "Updated state to $state")
                        }
                    }
                }
            }
        }

    }

    val engine = Engine(serverGameLogic, clientGameLogic, renderGameLogic)
    engine.run()
}