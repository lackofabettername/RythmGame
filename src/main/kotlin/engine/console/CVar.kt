package engine.console

import logging.Log
import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.function.Consumer

class CVar(
    val Name: String,
    val Type: CVarValueType
) {
    val Listeners = mutableListOf<Consumer<CVar>>()

    @Volatile
    var Text: String = ""
        set(value) {
            Dirty = true
            field = value
        }

    @Volatile
    var Value = 0
        set(value) {
            Dirty = true
            field = value
        }

    @Volatile
    var Flag = false
        set(value) {
            Dirty = true
            field = value
        }

    @Volatile
    var Dirty = false
        set(value) {
            if (value)
                Listeners.forEach { listener -> listener.accept(this) }

            field = value
        }

    var Clean: Boolean
        get() = !Dirty
        set(value) {
            Dirty = !value
        }

    fun clean(): CVar {
        Clean = true
        return this
    }

    operator fun get(type: CVarValueType) = when (type) {
        CVarValueType.Text -> Text
        CVarValueType.Value -> Value
        CVarValueType.Flag -> Flag
    }

    operator fun set(type: CVarValueType, data: Any) {
        when (type) {
            CVarValueType.Text -> Text = data as String
            CVarValueType.Value -> Value = data as Int
            CVarValueType.Flag -> Flag = data as Boolean
        }
        Dirty = true
    }

    infix fun set(data: Any) {
        when (Type) {
            CVarValueType.Text -> Text = data.toString()
            CVarValueType.Value -> Value = when (data) {
                is Number -> data.toInt()
                is String -> data.toInt()
                is Boolean -> if (data) 1 else 0
                else -> -1
            }
            CVarValueType.Flag -> Flag = when (data) {
                is Boolean -> data
                is Number -> data.toInt() > 0
                is String -> data.toBoolean()
                else -> false
            }
        }
        Dirty = true
    }

    fun get() = when (Type) {
        CVarValueType.Text -> Text
        CVarValueType.Value -> Value
        CVarValueType.Flag -> Flag
    }

    //region Constructors
    constructor(name: String, text: String) : this(name, CVarValueType.Text) {
        this.Text = text
    }

    constructor(name: String, value: Int) : this(name, CVarValueType.Value) {
        this.Value = value
    }

    constructor(name: String, flag: Boolean) : this(name, CVarValueType.Flag) {
        this.Flag = flag
    }
    //endregion
}