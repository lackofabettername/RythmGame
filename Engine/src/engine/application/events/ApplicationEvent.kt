package engine.application.events

import engine.console.ConsoleCommand

class ApplicationEvent(
    val Command: ConsoleCommand
) : InputEvent(InputEventType.Application) {
    override fun toString(): String {
        return "ApplicationEvent{${Command}}"
    }
}