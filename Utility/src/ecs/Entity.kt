package ecs

@JvmInline
value class Entity(
    private val data: Int
) {
    val id
        get() = data shr 16
    val ind
        get() = data and 0xFFFF

    constructor(ind: Int, id: Int) : this(ind or (id shl 16))

    override fun toString(): String {
        return "$ind - $id"
    }
}