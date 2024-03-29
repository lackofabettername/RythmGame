package engine

import engine.application.Application
import engine.application.RenderLogic
import engine.application.events.ApplicationEvent
import engine.application.events.InputEvent
import engine.application.events.InputEventType
import engine.console.Console
import engine.console.ConsoleCommand
import engine.console.logging.Log
import engine.console.logging.style.Foreground
import engine.console.logging.style.Style
import engine.events.SysEvent
import engine.events.SysEventManager
import engine.events.SysEventType
import engine.files.FileSystem
import engine.network.client.Client
import engine.network.client.ClientGameLogic
import engine.network.common.NetAddressable
import engine.network.common.NetManager
import engine.network.common.NetPacket
import engine.network.server.Server
import engine.network.server.ServerGameLogic
import util.misc.Checksum
import util.misc.toHexString
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.system.exitProcess

class Engine {

    //region Main engine modules
    internal var application: Application? = null

    private val _network: NetManager
    private var _server: Server? = null
    private var _client: Client? = null

    private val _events = SysEventManager(this)
    //endregion

    private val _commandQueue = ArrayBlockingQueue<ConsoleCommand>(512, true)

    private var _isServerDedicated = false
    private var _isRunning = false

    init {
        run {
            Locale.setDefault(Locale.ENGLISH) // Stupid Swedish computer uses , instead of . for floats

            val time = DateTimeFormatter.ofPattern("yyyy/mm/dd HH:mm:ss").format(LocalDateTime.now())

            Log.info("Engine", "$time, ${Checksum.digest(File("src/main/kotlin")).toHexString()}")
        }

        Log.info("Engine", "Initializing subsystems...")
        Log.Indent++

        Console.loadConfiguration()
        Console.registerCVarIfAbsent("sys_MaxRenderFPS", 0) // Uncapped
        Console.registerCVarIfAbsent("sys_VSync", false)

        //application = renderGameLogic?.let { Application(it, this) }
        //application?.initialize()

        _events.initialize()

        Log.info("Engine", "Initializing Network...")
        Log.Indent++
        _network = NetManager()
        //_server = serverGameLogic?.let { Server(_network, it) }
        //_client = clientGameLogic?.let { Client(_network, it, renderGameLogic!!) }
        Log.Indent--
        Log.info("Engine", "Initialized Network")

        //application?.start()

        _isRunning = true

        Log.Indent--
        Log.info("Engine", "Initialization complete.")
    }

    //region Engine hooks
    val Window get() = application?.Window

    var RenderLogic
        get() = application?.Logic
        set(logic) {
            if (application == null) {
                if (logic == null) return
                application = Application(this)
                application!!.init(logic)

            } else {
                if (logic != null)
                    application!!.init(logic)
                else {
                    application!!.close()
                    application = null
                }
            }
        }
    var ServerLogic
        get() = _server?.Logic
        set(logic) {
            if (_server == null) {
                if (logic == null) return //No difference
                _server = Server(_network, logic)

            } else {
                _server!!.shutdown()

                if (logic != null)
                    _server!!.init(logic)
                else
                    _server = null
            }
        }
    var ClientLogic
        get() = _client?.Logic
        set(logic) {
            if (_client == null) {
                if (logic == null) return //No difference
                _client = Client(_network, logic)

            } else {
                _client!!.shutdown()

                if (logic != null)
                    _client!!.init(logic)
                else
                    _client = null
            }
        }
    //endregion

    fun run() {
        var exitedSuccessfully = false
        try {
            mainLoop()
            exitedSuccessfully = true
        } catch (e: Throwable) {
            try {
                Log.Indent = 0
                Log.error("Engine", "A catastrophic error occurred.", e)
            } catch (ignored: Throwable) {
                System.err.println("A catastrophic error occurred.\n(printing again as the Logger may not be functional)")
                e.printStackTrace()
            }
        } finally {
            //TODO: Don't call this if the "exit" console command was triggered.
            shutdown(exitedSuccessfully)
        }
    }

    private fun shutdown(exitedSuccessfully: Boolean) {
        Log.Indent = 0

        Log.info("Engine", "Shutting down...")
        Log.Indent++

        Log.info("Engine", "Closing network...")
        Log.Indent++
        _client?.shutdown()
        _server?.shutdown()
        _network.close()
        Log.Indent--
        Log.info("Engine", "Network closed.")

        _events.close()

        Log.info("Engine", "Closing window...")
        Log.Indent++
        application?.close()
        Log.Indent--
        Log.info("Engine", "Window closed.")

        Console.close()
        FileSystem.close()
        _isRunning = false

        Log.Indent--
        Log.info("Engine", "Shutdown complete.")

        Log.info("Log", "Closing log file...")
        Log.close()

        exitProcess(if (exitedSuccessfully) 0 else 1)
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
                    _server?.let {
                        _network.consumeLoopbackPackets(it::onNetPacketReceived, NetAddressable.Server)
                    }
                    _client?.let {
                        _network.consumeLoopbackPackets(it::onNetPacketReceived, NetAddressable.Client)
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
                        application?.onInputEvent(event)

                    // _client.onInputEvent(event.Reference as InputEvent?)
                }

                SysEventType.Packet -> {
                    val packet = event.Reference as NetPacket
                    when (packet.TargetAddress.Addressable) {
                        NetAddressable.Server -> _server?.onNetPacketReceived(packet)
                        NetAddressable.Client -> _client?.onNetPacketReceived(packet)
                        NetAddressable.Unknown -> {
                            Log.warn("Engine", "NetPacket with unknown target. Sending to Server")
                            Log.trace("Engine", "$packet")
                            _server?.onNetPacketReceived(packet)
                        }
                    }
                }
            }

        } while (event.Type !== SysEventType.Empty)

        return event.Timestamp // The "current" time...

    }

    private fun executeSystemCommands() {
        //TODO: Map command text strings (defined in a table?) to function pointers.
        while (!_commandQueue.isEmpty()) {

            val command = _commandQueue.remove()
            Log.trace(
                "Engine",
                "Handling command: ${Foreground.Blue}${command.Text}(${
                    command.Args.contentDeepToString().drop(1).dropLast(1)
                })${Style.Reset}..."
            )

            with(command) {
                when (Text) {
                    "exit" -> _isRunning = false

                    "restart" -> {
                        if (Args.size == 1) {
                            when (Args[0]) {
                                "Application" -> {
                                    application?.close()
                                    application?.init(application!!.Logic)
                                }
                                else -> {
                                    Log.warn("Unknown keyword, ${Foreground.Blue}Application${Style.Reset} is supported.")
                                }
                            }
                        }
                    }

                    //"sv_shutdown" -> _server.shutdown()

                    else -> {
                        val cvar = Console.getCVar(Text)
                        if (cvar != null) {
                            when (Args.size) {
                                0 -> Log.info("Engine", "$cvar")
                                1 -> {
                                    cvar.set(Args[0])
                                    Log.info("Engine", "$cvar")
                                }
                                else -> Log.warn("Engine", "Unknown command")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mainLoop() {

        //region Loop Timing
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
        //endregion

        //WARN: The server cannot have a higher update rate than the application
        while (_isRunning) {
            Log.Indent = 0

            // ---------------------
            //  Core systems update
            // ---------------------
            if (application != null) _events.captureInputs() // Flush the input events once per frame...

            Console.writeConfiguration() //TODO: only write when CVars are updated?

            //Update loop timing if needed.
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

                if (eventTimeLast > eventTime) { // Possible on first frame of journal playback...
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
            _server?.update(deltaTime)

            // --------------------
            //  Client-side update
            // --------------------
            if (!_isServerDedicated && application != null) {
                processEventLoop() // Run again to avoid a frame of latency...
                executeSystemCommands()

                _client?.updateFrame(deltaTime)

                //TODO: Options for rendering the client-side simulation...
                // - Command the window to render and allow it to notify the client via interface or raw method call.
                // - Command the window to send a notification to a shared (with the client) simulation/game object.
                // - Pass the window to the client and forget about it. Then ask it to render.
                //TODO: Multi-thread the client updating and rendering? Research required.
                if (application!!.isRunning) {
                    application!!.update()
                    application!!.render()
                } else {
                    //TODO: Don't close the engine and keep running as server dedicated?
                    _isRunning = false
                }
            }
        }
    }
}