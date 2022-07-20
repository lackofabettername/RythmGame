package misc

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            close()
        } catch (closeException: Exception) {
            e.addSuppressed(closeException)
        }
        throw e
    } finally {
        if (!closed) {
            close()
        }
    }
}