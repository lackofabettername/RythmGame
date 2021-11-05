package engine.application.events

//TODO: Include which window it was sent from?
class KeyEvent(
    val Type: KeyEventType,
    val Key: Key
) : InputEvent(InputEventType.Key) {
    override fun toString(): String {
        return "KeyEvent{${Type.name}, $Key"
    }
}