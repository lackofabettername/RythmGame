package engine.console

import logging.Log
import logging.style.Font
import logging.style.Foreground
import logging.style.Style
import java.lang.AutoCloseable
import misc.StringSimilarity
import java.io.IOException
import java.lang.InterruptedException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.function.Consumer
import kotlin.collections.HashMap

@Suppress("unused")
class Console() : AutoCloseable {

    private val _commandQueue = ArrayBlockingQueue<ConsoleCommand>(QueueCapacity, true)
    private val _thread = Thread { mainLoop() }

    @Volatile
    private var _open = false
    private val _cVars = HashMap<String, CVar>()

    val cVars get() = _cVars.values

    fun initialize() {
        _thread.start()
    }

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

    fun registerCVar(cVar: CVar) {
        if (cVar.Name in _cVars)
            cVar.Listeners.addAll(_cVars[cVar.Name]!!.Listeners)
        _cVars[cVar.Name] = cVar
    }

    fun registerCVar(name: String, text: String) {
        registerCVar(CVar(name, text))
    }

    fun registerCVar(name: String, value: Int) {
        registerCVar(CVar(name, value))
    }

    fun registerCVar(name: String, flag: Boolean) {
        registerCVar(CVar(name, flag))
    }

    fun registerCVarIfAbsent(cVar: CVar) {
        _cVars.putIfAbsent(cVar.Name, cVar)
    }

    fun registerCVarIfAbsent(name: String, text: String) {
        registerCVarIfAbsent(CVar(name, text))
    }

    fun registerCVarIfAbsent(name: String, value: Int) {
        registerCVarIfAbsent(CVar(name, value))
    }

    fun registerCVarIfAbsent(name: String, flag: Boolean) {
        registerCVarIfAbsent(CVar(name, flag))
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

    companion object {
        const val QueueCapacity = 16
    }
}