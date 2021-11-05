package engine.events

import engine.files.FileAccessMode
import engine.files.FileSystem
import logging.Log

class SysEventJournal (mode: SysEventJournalMode){
    val DefaultPath = "EventJournal.ser"

    private val _filePath: String
    private var _disposed = false

    init {
        _filePath = DefaultPath
        when (mode) {
            SysEventJournalMode.Disabled -> _disposed = true
            SysEventJournalMode.Observer -> FileSystem.openFile(_filePath, FileAccessMode.Writer)
            SysEventJournalMode.Playback -> FileSystem.openFile(_filePath, FileAccessMode.Reader)
        }
    }

    fun dispose() {
        FileSystem.closeFile(_filePath)
        _disposed = true
    }

    fun read(): SysEvent {
        //TODO: When the file runs out of events?
        return if (_disposed) {
            Log.warn("SysManager", "Journal is disposed")
            SysEvent.empty
        } else FileSystem.readObject(_filePath) as SysEvent
    }

    fun write(event: SysEvent) {
        if (_disposed) {
            Log.warn("SysManager", "Journal is disposed")
            return
        }

        FileSystem.writeObject(_filePath, event)
    }
}