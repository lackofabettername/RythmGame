package space

import java.io.Serializable
import kotlin.math.*
import kotlin.random.Random

operator fun Array<Vector2>.times(value: Float) = this.map { it * value }.toTypedArray()

@Suppress("unused")
class Vector2(
    var x: Float = 0f,
    var y: Float = 0f
) : Serializable, Vector {

    //region Constructors
    constructor(v: Vector2) : this(v.x, v.y)

    constructor (values: FloatArray) : this(values[0], values[1])

    constructor (value: Float) : this(x = value, y = value)

    override val Dimension get() = 2
    //endregion

    //region Conversion
    val floatArray get() = floatArrayOf(x, y)

    operator fun component1() = x
    operator fun component2() = y
    //endregion

    //region Operations

    //region plus
    operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
    operator fun plus(value: Float): Vector2 = Vector2(x + value, y + value)
    operator fun plus(value: Int): Vector2 = Vector2(x + value, y + value)

    operator fun plusAssign(other: Vector2) {
        x += other.x
        y += other.y
    }

    operator fun plusAssign(value: Float) {
        x += value
        y += value
    }

    operator fun plusAssign(value: Int) {
        x += value
        y += value
    }

    fun plusAssign(x: Float, y: Float): Vector2 {
        this.x += x
        this.y += y
        return this
    }

    operator fun unaryPlus() = this
    //endregion

    //region minus
    operator fun minus(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    operator fun minus(value: Float) = Vector2(x - value, y - value)
    operator fun minus(value: Int) = Vector2(x - value, y - value)
    fun minus(x: Float, y: Float) = Vector2(this.x - x, this.y - y)
    fun minus(x: Int, y: Int) = Vector2(this.x - x, this.y - y)

    operator fun minusAssign(other: Vector2) {
        x -= other.x
        y -= other.y
    }

    operator fun minusAssign(value: Float) {
        x -= value
        y -= value
    }

    operator fun minusAssign(value: Int) {
        x -= value
        y -= value
    }

    operator fun unaryMinus() = Vector2(-x, -y)
    //endregion

    //region times
    operator fun times(other: Vector2): Vector2 {
        return Vector2(x * other.x, y * other.y)
    }

    operator fun times(factor: Float) = Vector2(x * factor, y * factor)
    operator fun times(factor: Int) = Vector2(x * factor, y * factor)
    fun times(x: Float, y: Float) = Vector2(this.x * x, this.y * y)

    operator fun timesAssign(other: Vector2) {
        x *= other.x
        y *= other.y
    }

    operator fun timesAssign(value: Float) {
        x *= value
        y *= value
    }

    operator fun timesAssign(value: Int) {
        x *= value
        y *= value
    }
    //endregion

    //region div
    operator fun div(other: Vector2) = Vector2(x / other.x, y / other.y)
    operator fun div(factor: Float) = Vector2(x / factor, y / factor)
    operator fun div(factor: Int) = Vector2(x / factor, y / factor)
    fun div(x: Float, y: Float) = Vector2(this.x / x, this.y / y)

    operator fun divAssign(other: Vector2) {
        x /= other.x
        y /= other.y
    }

    operator fun divAssign(value: Float) {
        x /= value
        y /= value
    }

    operator fun divAssign(value: Int) {
        x /= value
        y /= value
    }
    //endregion

    //region rem
    operator fun rem(other: Vector2) = Vector2(x % other.x, y % other.y)
    operator fun rem(value: Float) = Vector2(x % value, y % value)
    operator fun rem(value: Int) = Vector2(x % value, y % value)

    operator fun remAssign(other: Vector2) {
        x %= other.x
        y %= other.y
    }

    operator fun remAssign(value: Float) {
        x %= value
        y %= value
    }

    operator fun remAssign(value: Int) {
        x %= value
        y %= value
    }
    //endregion

    //region pow
    fun pow(other: Vector2) = Vector2(x.pow(other.x), y.pow(other.y))
    fun pow(value: Float) = Vector2(x.pow(value), y.pow(value))

    fun powAssign(other: Vector2): Vector2 {
        x = x.pow(other.x)
        y = y.pow(other.y)
        return this
    }

    fun powAssign(value: Float): Vector2 {
        x = x.pow(value)
        y = y.pow(value)
        return this
    }
    //endregion

    override operator fun get(axis: Int): Float {
        return when (axis % 2) {
            0 -> x
            1 -> y
            else -> 0f
        }
    }

    operator fun set(axis: Int, value: Float) {
        when (axis % 2) {
            0 -> x = value
            1 -> y = value
        }
    }

    fun lerp(v: Vector2, amount: Float): Vector2 {
        x = x * (1 - amount) + v.x * amount
        y = y * (1 - amount) + v.y * amount
        return this
    }

    fun coerceIn(min: Vector2, max: Vector2) = Vector2(
        x.coerceIn(min.x, max.x),
        y.coerceIn(min.y, max.y),
    )

    fun coerceIn(minX: Float, minY: Float, maxX: Float, maxY: Float) = Vector2(
        x.coerceIn(minX, maxX),
        y.coerceIn(minY, maxY),
    )

    fun wrap(minX: Float, minY: Float, maxX: Float, maxY: Float): Vector2 {
        while (x > maxX) x -= maxX - minX
        while (x < minX) x += maxX - minX
        while (y > maxY) y -= maxY - minY
        while (y < minY) y += maxY - minY
        return this
    }

    fun floor() = Vector2(floor(x), floor(y))
    fun round() = Vector2(round(x), round(y))

    fun floorAssign() {
        x = floor(x)
        y = floor(y)
    }

    fun roundAssign() {
        x = round(x)
        y = round(y)
    }

    //endregion

    //region Angles
    val heading get() = atan2(y, x)
    val perpendicular get() = Vector2(-y, x)

    fun rotate(angle: Float): Vector2 {
        val sin = sin(angle)
        val cos = cos(angle)
        return Vector2(
            x * cos - y * sin,
            x * sin + y * cos
        )
    }

    fun rotate(cosValue: Float, sinValue: Float): Vector2 {
        return Vector2(
            x * cosValue - y * sinValue,
            x * sinValue + y * cosValue
        )
    }

    fun rotateAssign(angle: Float): Vector2 {
        val sin = sin(angle)
        val cos = cos(angle)

        val newX = x * cos - y * sin
        val newY = x * sin + y * cos
        x = newX; y = newY
        return this
    }

    fun rotateAssign(cosValue: Float, sinValue: Float): Vector2 {
        val newX = x * cosValue - y * sinValue
        val newY = x * sinValue + y * cosValue
        x = newX; y = newY
        return this
    }

    infix fun dot(v: Vector2): Float {
        return x * v.x + y * v.y
    }

    fun dot(x: Float, y: Float): Float {
        return this.x * x + this.y * y
    }
    //endregion

    //region Magnitude
    val magnitudeSqr get() = x * x + y * y

    var magnitude
        get() = sqrt(magnitudeSqr)
        set(value) {
            if (magnitudeSqr > 0)
                timesAssign(value / magnitude)
        }

    //fun setMagnitude(mag: Float): Vector2 {
    //    val magnitude = magnitude
    //    return if (magnitude > 0) times(mag / magnitude) else this
    //}

    val normalized get() = if (this.magnitudeSqr != 0f) this.div(magnitude) else this
    fun normalize(): Vector2 {
        if (magnitude > 0)
            divAssign(magnitude)
        return this
    }

    fun distSqr(vector: Vector2) = minus(vector).magnitudeSqr
    fun dist(vector: Vector2) = minus(vector).magnitude
    //endregion

    //region Misc.
    val absolute get() = Vector2(x.absoluteValue, y.absoluteValue)

    infix fun copyFrom(v: Vector2): Vector2 {
        this.x = v.x
        this.y = v.y
        return this
    }

    infix fun copyInto(v: Vector2): Vector2 {
        v.x = x
        v.y = y
        return v
    }

    val copy get() = Vector2(this.x, this.y)

    fun clear(): Vector2 {
        x = 0f
        y = 0f
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector2) return false
        return x == other.x && y == other.y
    }

    fun about(v: Vector2, epsilon: Float): Boolean {
        return (this - v).magnitudeSqr < epsilon * epsilon
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
        val MAX_VALUE = Vector2(+Float.MAX_VALUE, +Float.MAX_VALUE)
        val MIN_VALUE = Vector2(-Float.MAX_VALUE, -Float.MAX_VALUE)

        //region Constructor
        fun random(rng: Random, magnitude: Float): Vector2 {
            return random(rng) * magnitude
        }

        fun random(rng: Random): Vector2 {
            return (Vector2(
                rng.nextFloat(),
                rng.nextFloat(),
            ) * 2 - 1).normalize()
        }

        fun random(magnitude: Float = 1f) = Vector2(
            Math.random().toFloat() * 2 - 1,
            Math.random().toFloat() * 2 - 1,
        ).normalize() * magnitude

        fun fromAngle(angle: Float, length: Float = 1f) = Vector2(
            cos(angle) * length,
            sin(angle) * length
        )

        fun arrayOf(vararg values: Float) = values.toList()
            .chunked(2)
            .map { Vector2(it[0], it.getOrElse(1) { 0f }) }
            .toTypedArray()
        //endregion

        fun lerp(amount: Float, a: Vector2, b: Vector2): Vector2 {
            return Vector2(
                a.x * (1f - amount) + b.x * amount,
                a.y * (1f - amount) + b.y * amount,
            )
        }

        fun wrap(v: Vector2, minX: Float, minY: Float, maxX: Float, maxY: Float): Vector2 {
            val result = Vector2(v)
            while (result.x > maxX) result.x -= maxX - minX
            while (result.x < minX) result.x += maxX - minX
            while (result.y > maxY) result.y -= maxY - minY
            while (result.y < minY) result.y += maxY - minY
            return result
        }

        fun sum(vararg vectors: Vector2): Vector2 {
            val result = Vector2()
            for (v in vectors) result += v
            return result
        }

        fun average(vararg vectors: Vector2): Vector2 {
            return sum(*vectors) / vectors.size
        }

        fun max(vararg vectors: Vector2): Vector2 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MIN_VALUE.copy
            for (v in vectors) {
                result.x.coerceAtLeast(v.x)
                result.y.coerceAtLeast(v.y)
            }
            return result
        }

        fun max(v: Vector2, x: Float, y: Float): Vector2 {
            return Vector2(
                max(v.x, x),
                max(v.y, y),
            )
        }

        fun max(x: Float, y: Float, v: Vector2): Vector2 {
            return Vector2(
                max(v.x, x),
                max(v.y, y),
            )
        }

        fun min(vararg vectors: Vector2): Vector2 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MAX_VALUE.copy
            for (v in vectors) {
                result.x.coerceAtMost(v.x)
                result.y.coerceAtMost(v.y)
            }
            return result
        }

        fun min(v: Vector2, x: Float, y: Float): Vector2 {
            return Vector2(
                min(v.x, x),
                min(v.y, y),
            )
        }

        fun min(x: Float, y: Float, v: Vector2): Vector2 {
            return Vector2(
                min(v.x, x),
                min(v.y, y),
            )
        }
    }
}