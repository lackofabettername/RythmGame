package engine.application.events

import java.io.Serializable

//TODO: Put more event information here like time?
//TODO: Controller support
abstract class InputEvent(val EventType: InputEventType) : Serializable