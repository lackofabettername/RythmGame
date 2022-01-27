package engine.application.events

enum class MouseEventType {
    Moved,  //x, y

    ButtonPressed,  //button
    ButtonReleased,  //button

    Wheel,  //xoffset, yoffset

    Enter, //true/false

    Unkown
}