package engine.application.events

//TODO: include which window it was sent from?
@Suppress("PropertyName")
class MouseEvent private constructor(
    val Type: MouseEventType,
    private val _valueA: Int,
    private val _valueB: Int,
    dummy: Boolean
) : InputEvent(InputEventType.Mouse) {

    val X get() = Float.fromBits(_valueA)
    val Y get() = Float.fromBits(_valueB)

    val Button get() = _valueA
    val Mods get() = Modifiers(_valueB)

    val InsideWindow get() = _valueA > 0

    constructor(type: MouseEventType, x: Float, y: Float) : this(
        type,
        x.toBits(),
        y.toBits(),
        false
    )

    constructor(type: MouseEventType, button: Int, mods: Int) : this(
        type,
        button,
        mods,
        false
    )

    constructor(type: MouseEventType, entered: Boolean) : this(
        type,
        if (entered) 1 else 0,
        0,
        false
    )

    override fun toString(): String {
        return "MouseEvent{${Type.name}: " + when (Type) {
            MouseEventType.Moved -> "($X, $Y)"
            MouseEventType.ButtonPressed -> "$Button, $Mods"
            MouseEventType.ButtonReleased -> "$Button, $Mods"
            MouseEventType.Wheel -> "($X, $Y)"
            MouseEventType.Enter -> "$InsideWindow"
            MouseEventType.Unkown -> ""
        } + "}"
    }
}