package engine.events

enum class SysEventJournalMode {
    Disabled,  // Gameplay: Do nothing....
    Observer,  // Gameplay: Write passing events to file.
    Playback // Playback: Read events from file.
}