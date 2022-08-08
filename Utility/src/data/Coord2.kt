package data

import space.Vector2
import java.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

val Pair<Int, Int>.coord get() = Coord2(first, second)

@Suppress("unused")
class Coord2(
    var x: Int = 0,
    var y: Int = 0
) : Serializable, Coord {

    //region Constructors
    constructor(v: Coord2) : this(v.x, v.y)

    constructor (values: IntArray) : this(values[0], values[1])

    constructor (value: Int) : this(x = value, y = value)

    override val Dimension get() = 2
    //endregion

    //region Conversion
    val intArray get() = intArrayOf(x, y)

    operator fun component1() = x
    operator fun component2() = y

    val vector get() = Vector2(x.toFloat(), y.toFloat())
    //endregion

    //region Operations

    //region plus
    operator fun plus(other: Coord2) = Coord2(x + other.x, y + other.y)
    operator fun plus(vector: Vector2) = Vector2(x + vector.x, y + vector.y)
    operator fun plus(value: Int) = Coord2(x + value, y + value)

    operator fun plusAssign(other: Coord2) {
        x += other.x
        y += other.y
    }

    operator fun plusAssign(value: Int) {
        x += value
        y += value
    }

    fun plusAssign(x: Int, y: Int): Coord2 {
        this.x += x
        this.y += y
        return this
    }

    operator fun unaryPlus() = this
    //endregion

    //region minus
    operator fun minus(other: Coord2): Coord2 {
        return Coord2(x - other.x, y - other.y)
    }

    operator fun minus(value: Int) = Coord2(x - value, y - value)
    fun minus(x: Int, y: Int) = Coord2(this.x - x, this.y - y)

    operator fun minusAssign(other: Coord2) {
        x -= other.x
        y -= other.y
    }

    operator fun minusAssign(value: Int) {
        x -= value
        y -= value
    }

    operator fun unaryMinus() = Coord2(-x, -y)
    //endregion

    //region times
    operator fun times(other: Coord2): Coord2 {
        return Coord2(x * other.x, y * other.y)
    }
    operator fun times(vector: Vector2) = Vector2(x * vector.x, y * vector.y)


    operator fun times(factor: Int) = Coord2(x * factor, y * factor)
    operator fun times(factor: Float) = Vector2(x * factor, y * factor)
    fun times(x: Int, y: Int) = Coord2(this.x * x, this.y * y)
    fun times(x: Float, y: Float) = Vector2(this.x * x, this.y * y)

    operator fun timesAssign(other: Coord2) {
        x *= other.x
        y *= other.y
    }

    operator fun timesAssign(value: Int) {
        x *= value
        y *= value
    }
    //endregion

    //region div
    operator fun div(other: Coord2) = Coord2(x / other.x, y / other.y)
    operator fun div(factor: Int) = Coord2(x / factor, y / factor)
    fun div(x: Int, y: Int) = Coord2(this.x / x, this.y / y)

    operator fun divAssign(other: Coord2) {
        x /= other.x
        y /= other.y
    }

    operator fun divAssign(value: Int) {
        x /= value
        y /= value
    }

    //endregion

    //region rem
    operator fun rem(other: Coord2) = Coord2(x % other.x, y % other.y)
    operator fun rem(value: Int) = Coord2(x % value, y % value)

    operator fun remAssign(other: Coord2) {
        x %= other.x
        y %= other.y
    }

    operator fun remAssign(value: Int) {
        x %= value
        y %= value
    }
    //endregion

    override operator fun get(axis: Int): Int {
        return when (axis % 2) {
            0 -> x
            1 -> y
            else -> 0
        }
    }

    operator fun set(axis: Int, value: Int) {
        when (axis % 2) {
            0 -> x = value
            1 -> y = value
        }
    }

    fun coerceIn(min: Coord2, max: Coord2) = Coord2(
        x.coerceIn(min.x, max.x),
        y.coerceIn(
            min.y, max.y
        )
    )

    fun coerceIn(minX: Int, minY: Int, maxX: Int, maxY: Int) = Coord2(
        x.coerceIn(minX, maxX),
        y.coerceIn(minY, maxY)
    )
    //endregion

    //region Misc.
    val absolute get() = Coord2(x.absoluteValue, y.absoluteValue)

    infix fun copyFrom(v: Coord2): Coord2 {
        this.x = v.x
        this.y = v.y
        return this
    }

    infix fun copyInto(v: Coord2): Coord2 {
        v.x = x
        v.y = y
        return v
    }

    val copy get() = Coord2(this.x, this.y)

    fun clear(): Coord2 {
        x = 0
        y = 0
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Coord2) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "($x, $y)"
    }
    //endregion

    companion object {
        val MAX_VALUE = Coord2(Int.MAX_VALUE, Int.MAX_VALUE)
        val MIN_VALUE = Coord2(Int.MIN_VALUE, Int.MIN_VALUE)

        val axis = arrayOf(Coord2(1, 0), Coord2(0, 1))

        //region Constructor
        fun random(x: IntRange, y: IntRange) = Coord2(
            Random.nextInt() * (x.endInclusive - x.start) + x.start,
            Random.nextInt() * (y.endInclusive - y.start) + y.start,
        )

        fun arrayOf(vararg values: Int) = values.toList()
            .chunked(2)
            .map { Coord2(it[0], it.getOrElse(1) { 0 }) }
            .toTypedArray()
        //endregion

        fun sum(vararg vectors: Coord2): Coord2 {
            val result = Coord2()
            for (v in vectors) result += v
            return result
        }

        fun average(vararg vectors: Coord2): Coord2 {
            return sum(*vectors) / vectors.size
        }

        fun max(vararg vectors: Coord2): Coord2 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MIN_VALUE.copy
            for (v in vectors) {
                result.x.coerceAtLeast(v.x)
                result.y.coerceAtLeast(v.y)
            }
            return result
        }

        fun max(v: Coord2, x: Int, y: Int): Coord2 {
            return Coord2(
                max(v.x, x),
                max(v.y, y),
            )
        }

        fun max(x: Int, y: Int, v: Coord2): Coord2 {
            return Coord2(
                max(v.x, x),
                max(v.y, y),
            )
        }

        fun min(vararg vectors: Coord2): Coord2 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MAX_VALUE.copy
            for (v in vectors) {
                result.x.coerceAtMost(v.x)
                result.y.coerceAtMost(v.y)
            }
            return result
        }

        fun min(v: Coord2, x: Int, y: Int): Coord2 {
            return Coord2(
                min(v.x, x),
                min(v.y, y),
            )
        }

        fun min(x: Int, y: Int, v: Coord2): Coord2 {
            return Coord2(
                min(v.x, x),
                min(v.y, y),
            )
        }
    }
}