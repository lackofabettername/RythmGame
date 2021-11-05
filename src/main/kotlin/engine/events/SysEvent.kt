package engine.events

import java.io.Serializable

class SysEvent(
    val Type: SysEventType,
    val Timestamp: Long,
    val Reference: Serializable?
) : Serializable {
    override fun toString(): String {
        return "SysEvent{Type=$Type, Timestamp=$Timestamp, Reference=$Reference}"
    }

    companion object {
        @JvmStatic
        val empty: SysEvent
            get() = SysEvent(SysEventType.Empty, System.currentTimeMillis(), null)
    }
}