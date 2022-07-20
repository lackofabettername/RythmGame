package engine.console

import engine.console.CVarValueType.*
import engine.console.logging.Log
import engine.console.logging.style.Font
import engine.console.logging.style.Foreground
import engine.console.logging.style.Style
import engine.files.FileAccessMode
import engine.files.FileSystem
import misc.StringSimilarity
import misc.use
import java.io.IOException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer
import kotlin.concurrent.thread

@Suppress("unused")
object Console : AutoCloseable {

    const val QueueCapacity = 16

    private val _commandQueue = ArrayBlockingQueue<ConsoleCommand>(QueueCapacity, true)
    private val _thread = thread(
        true,
        false,
        null,
        "Console listener"
    ) { mainLoop() }

    @Volatile
    private var _open = false
    private val _cVars = HashMap<String, CVar>()

    @Volatile
    var UpdateConfiguration = false

    val CVars get() = _cVars.values

    fun rerouteCommands(callback: Consumer<ConsoleCommand>) {
        while (!_commandQueue.isEmpty())
            callback.accept(_commandQueue.poll())
    }

    override fun close() {
        _open = false
        _commandQueue.clear()
        _thread.interrupt()

        //TODO:
        // -- Use join() instead with a shared flag so the main loop knows when to close.
        // -- If that fails or it's stuck for some reason, maybe use a timeout mechanism... then blast it.
    }

    //region Register CVar

    fun registerCVar(cVar: CVar): CVar {
        if (cVar.Name in _cVars)
            cVar.Listeners.addAll(_cVars[cVar.Name]!!.Listeners)
        _cVars[cVar.Name] = cVar
        return cVar
    }

    fun registerCVar(name: String, text: String) = CVar(name, text).also {
        registerCVar(it)
    }

    fun registerCVar(name: String, value: Int) = CVar(name, value).also {
        registerCVar(it)
    }

    fun registerCVar(name: String, flag: Boolean) = CVar(name, flag).also {
        registerCVar(it)
    }

    fun registerCVarIfAbsent(cVar: CVar): CVar {
        _cVars.putIfAbsent(cVar.Name, cVar)
        return cVar
    }

    fun registerCVarIfAbsent(name: String, text: String) = CVar(name, text).also {
        registerCVarIfAbsent(it)
    }

    fun registerCVarIfAbsent(name: String, value: Int) = CVar(name, value).also {
        registerCVarIfAbsent(it)
    }

    fun registerCVarIfAbsent(name: String, flag: Boolean) = CVar(name, flag).also {
        registerCVarIfAbsent(it)
    }

    //endregion

    fun removeCVar(name: String): Boolean {
        return _cVars.remove(name) != null
    }

    fun getCVar(name: String): CVar? {
        if (name !in _cVars) {
            Log.warn(
                "Console",
                "There is no CVar with the name ${Font.Italics}${Foreground.LightGrey}$name${Style.Reset}."
            )

            //Find CVar with similar Name

            val comparator = compareBy<Pair<Float, String>> { it.first }.reversed()
            val result = PriorityQueue<Pair<Float, String>>(comparator)
            _cVars.forEach { (key, _) ->
                val similarity = StringSimilarity
                    .diceCoefficientOptimized(name, key)
                    .toFloat()
                result.add(similarity to key)
            }

            if ((result.peek()?.first ?: 0f) > 0.1) {
                Log.warn(
                    "Console",
                    "There is a CVar with a similar name: ${Font.Italics}${Foreground.Green}${
                        result.poll().second
                    }${Style.Reset}."
                )
            }
        }

        return _cVars[name]
    }

    //region Update
    fun updateCVar(name: String): CVar? {
        val cVar = _cVars[name]
        cVar?.Dirty = true
        return cVar
    }

    fun updateCVar(name: String, text: String): CVar? {
        val cVar = _cVars[name]
        if (cVar != null) {
            cVar.Dirty = true
            cVar.Text = text
        }
        return cVar
    }

    fun updateCVar(name: String, value: Int): CVar? {
        val cVar = _cVars[name]
        if (cVar != null) {
            cVar.Dirty = true
            cVar.Value = value
        }
        return cVar
    }

    fun updateCVar(name: String, flag: Boolean): CVar? {
        val cVar = _cVars[name]
        if (cVar != null) {
            cVar.Dirty = true
            cVar.Flag = flag
        }
        return cVar
    }

    //endregion

    fun loadConfiguration() {
        //TODO:
        // - Read console variables from a file.
        // - Undecided: Variables must already exist.
        // --- Defined by a game-specific manifest
        // --- or from some kind of core systems registry.

        Log.info("FileSystem", "Loading Configuration...")
        Log.Indent++

        FileSystem.openFile("System.CFG", FileAccessMode.Read)?.use { inp ->
            inp.Reader.forEachLine { line ->
                Log.trace("Console", line)

                if (line.startsWith("CVar")) {
                    val parts = line.split(", ")
                    val name = parts[0].drop(5)
                    val data = parts[2].replace(Regex("}.*"), "")
                    val cvar = when (parts[1]) {
                        //@formatter:off
                        Text.name  -> CVar(name, data)
                        Value.name -> CVar(name, data.toInt())
                        Flag.name  -> CVar(name, data.toBoolean())
                        //@formatter:on
                        else -> null
                    }
                    if (cvar != null)
                        registerCVar(cvar)
                }
            }
            inp.Writer.flush()
        }

        Log.Indent--
        Log.info("FileSystem", "Configuration Loaded")
    }

    fun writeConfiguration() {
        //TODO:
        // - Write out the settings of the engine or game to a file...
        // - Implement methods for saving out game state data?

        if (!UpdateConfiguration) return

        if ("System.CFG" !in FileSystem) {
            FileSystem.openFile("System.CFG", FileAccessMode.Write, true)
        }

        //todo: replace use with let and only update the needed parts, instead of clearing everything and rewriting it
        FileSystem["System.CFG"]?.use { out ->
            //Print all CVars in alphabetical order
            for (cvar in CVars
                .stream()
                .sorted(Comparator.comparing { s -> s.Name.lowercase() })
            ) {
                out.Writer.write("CVar{${cvar.Name}, ${cvar.Type}, ${cvar.get()}}\n")
            }
        }

        UpdateConfiguration = false
    }


    private fun mainLoop() {
        val input = Scanner(System.`in`)
        input.useDelimiter("[ \t]")
        _open = true

        while (_open) {

            //To avoid blocking the program.
            //Once something reads from System.in It is impossible to interrupt.
            try {
                while (System.`in`.available() == 0) {
                    Thread.sleep(200)
                }
            } catch (e: IOException) {
                Log.error("Console", e)
            } catch (ignored: InterruptedException) {
                return
            }

            val text = input.nextLine()
            val tokens = text.split("[ \t]".toRegex()).toTypedArray()
            val command = tokens[0]
            var args = arrayOf<String>()

            if (tokens.size > 1) {
                args = tokens.sliceArray(1 until tokens.size)
                Log.trace("Console", "Read command: $command. Args: ${args.contentToString()}")
            }

            if (!_commandQueue.offer(ConsoleCommand(command, *args))) {
                Log.warn("Console", "Command overflow!")
            }
        }
    }
}