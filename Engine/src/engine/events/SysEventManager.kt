package engine.events

import engine.Engine
import engine.console.ConsoleCommand
import engine.console.logging.Log
import java.util.concurrent.ArrayBlockingQueue

class SysEventManager(private val _engine: Engine) {
    private val _eventQueue = ArrayBlockingQueue<SysEvent>(QueueCapacity, true)
    private var _journalMode: SysEventJournalMode = SysEventJournalMode.Disabled
    private lateinit var _journal: SysEventJournal

    fun initialize() {
        _eventQueue.clear()
        _journal = SysEventJournal(_journalMode)
    }

    fun close() {
        _eventQueue.clear()
        _journalMode = SysEventJournalMode.Disabled
        _journal.dispose()
    }

    //region Event capture methods
    fun captureCommands() {
        _engine.Console.rerouteCommands { command: ConsoleCommand ->
            enqueueEvent(
                SysEvent(SysEventType.ConsoleCommand, 0, command)
            )
        }
    }

    fun captureMessages() {
        //_engine.Network.consumeInternetPackets { message -> enqueueEvent(SysEvent(SysEventType.Packet, 0, message)) }
    }

    fun captureInputs() {
        _engine.Application!!.pollEvents()
        _engine.Application.rerouteInputEvents { input -> enqueueEvent(SysEvent(SysEventType.Input, 0, input)) }
    }

    fun enqueueEvent(event: SysEvent) {
        if (_journalMode != SysEventJournalMode.Playback) {
            if (event.Reference == null) {
                Log.warn("SysEventManager", "Event reference is null!")
            } else if (!_eventQueue.offer(event)) {
                Log.warn("SysEventManager", "Queue overflow!")
            }
        }
    }
    //endregion

    fun getEvent() = when (_journalMode) {
        SysEventJournalMode.Disabled -> dequeueEvent()
        SysEventJournalMode.Observer -> observeEvent()
        SysEventJournalMode.Playback -> readoutEvent()
    }

    private fun dequeueEvent(): SysEvent {
        // Pump the channels if necessary
        if (_eventQueue.isEmpty()) {
            captureCommands()
            captureMessages()
        }
        return if (_eventQueue.isEmpty()) SysEvent.empty else _eventQueue.remove()
    }

    private fun observeEvent(): SysEvent {
        val event = dequeueEvent()
        _journal.write(event)
        return event
    }

    private fun readoutEvent() = _journal.read()

    companion object {
        const val QueueCapacity = 1024
    }
}