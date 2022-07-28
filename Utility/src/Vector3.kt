@file:Suppress("unused")

package util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
class Vector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) : Vector {

    //region Constructors
    constructor(v: Vector3) : this(v.x, v.y, v.z)

    constructor(v: Vector2, z: Float) : this(v.x, v.y, z)

    constructor (values: FloatArray) : this(values[0], values[1], values[2])

    constructor (value: Float) : this(x = value, y = value, z = value)

    override fun getDimension() = 3

    //endregion

    //region Conversion
    fun xy(): Vector2 {
        return Vector2(x, y)
    }

    fun yz(): Vector2 {
        return Vector2(y, z)
    }

    val floatArray get() = floatArrayOf(x, y, z)
    //endregion

    //region Operations

    //region plus
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun plus(value: Float) = Vector3(x + value, y + value, z + value)
    operator fun plus(value: Int) = Vector3(x + value, y + value, z + value)

    operator fun plusAssign(other: Vector3) {
        x += other.x
        y += other.y
        z += other.z
    }

    operator fun plusAssign(value: Float) {
        x += value
        y += value
        z += value
    }

    operator fun plusAssign(value: Int) {
        x += value
        y += value
        z += value
    }

    fun plusAssign(x: Float, y: Float, z: Float): Vector3 {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    operator fun unaryPlus() = this
    //endregion

    //region minus
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun minus(value: Float) = Vector3(x - value, y - value, z - value)
    operator fun minus(value: Int) = Vector3(x - value, y - value, z - value)

    operator fun minusAssign(other: Vector3) {
        x -= other.x
        y -= other.y
        z -= other.z
    }

    operator fun minusAssign(value: Float) {
        x -= value
        y -= value
        z -= value
    }

    operator fun minusAssign(value: Int) {
        x -= value
        y -= value
        z -= value
    }

    operator fun unaryMinus() = Vector3(-x, -y, -z)
    //endregion

    //region times
    operator fun times(other: Vector3) = Vector3(x * other.x, y * other.y, z * other.z)
    operator fun times(factor: Float) = Vector3(x * factor, y * factor, z * factor)
    operator fun times(factor: Int) = Vector3(x * factor, y * factor, z * factor)

    operator fun timesAssign(other: Vector3) {
        x *= other.x
        y *= other.y
        z *= other.z
    }

    operator fun timesAssign(value: Float) {
        x *= value
        y *= value
        z *= value
    }

    operator fun timesAssign(value: Int) {
        x *= value
        y *= value
        z *= value
    }
    //endregion

    //region div
    operator fun div(other: Vector3) = Vector3(x / other.x, y / other.y, z / other.z)
    operator fun div(factor: Float) = Vector3(x / factor, y / factor, z / factor)
    operator fun div(factor: Int) = Vector3(x / factor, y / factor, z / factor)

    operator fun divAssign(other: Vector3) {
        x /= other.x
        y /= other.y
        z /= other.z
    }

    operator fun divAssign(value: Float) {
        x /= value
        y /= value
        z /= value
    }

    operator fun divAssign(value: Int) {
        x /= value
        y /= value
        z /= value
    }
    //endregion

    //region rem
    operator fun rem(other: Vector3) = Vector3(x % other.x, y % other.y, z % other.z)
    operator fun rem(value: Float) = Vector3(x % value, y % value, z % value)
    operator fun rem(value: Int) = Vector3(x % value, y % value, z % value)

    operator fun remAssign(other: Vector3) {
        x %= other.x
        y %= other.y
        z %= other.z
    }

    operator fun remAssign(value: Float) {
        x %= value
        y %= value
        z %= value
    }

    operator fun remAssign(value: Int) {
        x %= value
        y %= value
        z %= value
    }
    //endregion

    override operator fun get(axis: Int): Float {
        require(axis in 0..2)
        return when (axis) {
            0 -> x
            1 -> y
            2 -> z
            else -> 0f
        }
    }

    operator fun set(axis: Int, value: Float) {
        require(axis in 0..2)
        when (axis) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
        }
    }

    fun cross(v: Vector3): Vector3 {
        return Vector3(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x,
        )
    }

    fun crossAssign(v: Vector3): Vector3 {
        val x1 = y * v.z - z * v.y
        val y1 = z * v.x - x * v.z
        val z1 = x * v.y - y * v.x

        x = x1; y = y1; z = z1

        return this
    }

    //endregion

    //region Angles
    fun dot(v: Vector3): Float {
        return x * v.x + y * v.y + z * v.z
    }

    fun dot(x: Float, y: Float, z: Float): Float {
        return this.x * x + this.y * y + this.z * z
    }
    //endregion

    //region Magnitude
    //optimize: only update when x,y or z component changes?
    val magnitudeSqr get() = x * x + y * y + z * z

    var magnitude
        get() = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        set(value) {
            if (magnitude > 0)
                timesAssign(value / magnitude)
        }

    fun setMagnitude(mag: Float): Vector3 {
        val magnitude = magnitude
        return if (magnitude > 0) times(mag / magnitude) else this
    }

    val normalized; get() = this / magnitude
    fun normalize(): Vector3 {
        divAssign(magnitude)
        return this
    }

    fun distSqr(vector: Vector3): Float {
        return minus(vector).magnitudeSqr
    }

    fun dist(vector: Vector3): Float {
        return minus(vector).magnitude
    }
    //endregion

    //region Misc.
    fun set(v: Vector3): Vector3 {
        set(v.x, v.y, v.z)
        return this
    }

    fun set(x: Float, y: Float, z: Float): Vector3 {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun clear(): Vector3 {
        return set(0f, 0f, 0f)
    }

    fun lerp(v: Vector3, amount: Float): Vector3 {
        return lerp(this, v, amount)
    }

    fun constrain(min: Vector3, max: Vector3): Vector3 {
        x.coerceIn(min.x, max.x)
        y.coerceIn(min.y, max.y)
        z.coerceIn(min.z, max.z)
        return this
    }

    fun constrain(minX: Float, minY: Float, maxX: Float, maxY: Float, minZ: Float, maxZ: Float): Vector3 {
        x.coerceIn(minX, maxX)
        y.coerceIn(minY, maxY)
        z.coerceIn(minZ, maxZ)
        return this
    }

    fun wrap(minX: Float, minY: Float, maxX: Float, maxY: Float, minZ: Float, maxZ: Float): Vector3 {
        while (x > maxX) x -= maxX - minX
        while (x < minX) x += maxX - minX
        while (y > maxY) y -= maxY - minY
        while (y < minY) y += maxY - minY
        while (z > maxZ) z -= maxZ - minZ
        while (z < minZ) z += maxZ - minZ
        return this
    }

    fun copy(): Vector3 {
        return Vector3(this.x, this.y, this.z)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector3) return false
        return x == other.x && y == other.y && z == other.z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    fun about(v: Vector3, epsilon: Float): Boolean {
        return (this - v).magnitudeSqr < epsilon * epsilon
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
    //endregion


    companion object {
        val MAX by lazy { Vector3(+Float.MAX_VALUE, +Float.MAX_VALUE, +Float.MAX_VALUE) }
        val MIN by lazy { Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE) }

        fun random(rng: Random, magnitude: Float): Vector3 {
            return random(rng) * magnitude
        }

        fun random(rng: Random): Vector3 {
            return (Vector3(
                rng.nextFloat(),
                rng.nextFloat(),
                rng.nextFloat(),
            ) * 2 - 1).normalize()
        }

        fun random(magnitude: Float): Vector3 {
            return random() * magnitude
        }

        fun random(): Vector3 {
            return (Vector3(
                Math.random().toFloat(),
                Math.random().toFloat(),
                Math.random().toFloat()
            ) * 2 - 1).normalize()
        }

        fun arrayOf(vararg values: Float) = values.toList()
            .chunked(3)
            .map { Vector3(it[0], it.getOrElse(1) { 0f }, it.getOrElse(2) { 0f }) }
            .toTypedArray()

        fun toFloatArray(v: Vector3): FloatArray {
            return floatArrayOf(v.x, v.y, v.z)
        }

        fun cross(a: Vector3, b: Vector3): Vector3 {
            return Vector3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x,
            )
        }

        fun lerp(a: Vector3, b: Vector3, amount: Float): Vector3 {
            return Vector3(
                a.x * (1f - amount) + b.x * amount,
                a.y * (1f - amount) + b.y * amount,
                a.z * (1f - amount) + b.z * amount
            )
        }

        fun wrap(v: Vector3, minX: Float, minY: Float, maxX: Float, maxY: Float, minZ: Float, maxZ: Float): Vector3 {
            val result = Vector3(v)
            while (result.x > maxX) result.x -= maxX - minX
            while (result.x < minX) result.x += maxX - minX
            while (result.y > maxY) result.y -= maxY - minY
            while (result.y < minY) result.y += maxY - minY
            while (result.z > maxZ) result.z -= maxZ - minZ
            while (result.z < minZ) result.z += maxZ - minZ
            return result
        }

        fun constrain(
            v: Vector3,
            minX: Float,
            minY: Float,
            maxX: Float,
            maxY: Float,
            minZ: Float,
            maxZ: Float
        ): Vector3 {
            return min(max(v, minX, minY, minZ), maxX, maxY, maxZ)
        }

        fun constrain(v: Vector3, min: Vector3, max: Vector3): Vector3 {
            return min(max(v, min), max)
        }

        fun sum(vararg vectors: Vector3): Vector3 {
            val result = Vector3()
            for (v in vectors) result += v
            return result
        }

        fun average(vararg vectors: Vector3): Vector3 {
            return sum(*vectors) / vectors.size
        }

        fun max(vararg vectors: Vector3): Vector3 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MIN.copy()
            for (v in vectors) {
                result.x.coerceAtLeast(v.x)
                result.y.coerceAtLeast(v.y)
                result.z.coerceAtLeast(v.z)
            }
            return result
        }

        fun max(v: Vector3, x: Float, y: Float, z: Float): Vector3 {
            return Vector3(
                max(v.x, x),
                max(v.y, y),
                max(v.z, z)
            )
        }

        fun max(x: Float, y: Float, z: Float, v: Vector3): Vector3 {
            return Vector3(
                max(v.x, x),
                max(v.y, y),
                max(v.z, z)
            )
        }

        fun min(vararg vectors: Vector3): Vector3 {
            require(vectors.isNotEmpty()) { "There must be more than 0 Vectors" }
            val result = MAX.copy()
            for (v in vectors) {
                result.x.coerceAtMost(v.x)
                result.y.coerceAtMost(v.y)
                result.z.coerceAtMost(v.z)
            }
            return result
        }

        fun min(v: Vector3, x: Float, y: Float, z: Float): Vector3 {
            return Vector3(
                min(v.x, x),
                min(v.y, y),
                min(v.z, z)
            )
        }

        fun min(x: Float, y: Float, z: Float, v: Vector3): Vector3 {
            return Vector3(
                min(v.x, x),
                min(v.y, y),
                min(v.z, z)
            )
        }


        fun dot(a: Vector3, b: Vector3): Float {
            return a.x * b.x + a.y * b.y + a.z * b.z
        }

        fun magnitudeSqr(v: Vector3): Float {
            return v.x * v.x + v.y * v.y + v.z * v.z
        }

        fun magnitude(v: Vector3): Float {
            return sqrt((v.x * v.x + v.y * v.y + v.z * v.z).toDouble()).toFloat()
        }

        fun setMagnitude(v: Vector3, mag: Float): Vector3 {
            return Vector3(v).setMagnitude(mag)
        }

        fun normalize(v: Vector3): Vector3 {
            return Vector3(v).normalize()
        }

        fun distSqr(a: Vector3, b: Vector3): Float {
            return (a - b).magnitudeSqr
        }

        fun distSqr(v: Vector3, x: Float, y: Float, z: Float): Float {
            return (v - Vector3(x, y, z)).magnitudeSqr
        }

        fun distSqr(x: Float, y: Float, z: Float, v: Vector3): Float {
            return (v - Vector3(x, y, z)).magnitudeSqr
        }

        fun distSqr(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float {
            return (Vector3(x1, y1, z1) - Vector3(x2, y2, z2)).magnitudeSqr
        }

        fun dist(a: Vector3, b: Vector3): Float {
            return (a - b).magnitude
        }

        fun dist(v: Vector3, x: Float, y: Float, z: Float): Float {
            return (v - Vector3(x, y, z)).magnitude
        }

        fun dist(x: Float, y: Float, z: Float, v: Vector3): Float {
            return (v - Vector3(x, y, z)).magnitude
        }

        fun dist(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float {
            return (Vector3(x1, y1, z1) - Vector3(x2, y2, z2)).magnitude
        }

        fun about(a: Vector3, b: Vector3, epsilon: Float): Boolean {
            return (a - b).magnitudeSqr < epsilon * epsilon
        }
    }
}