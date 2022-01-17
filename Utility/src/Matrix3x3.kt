package util

import java.util.*

class Matrix3x3(
    val values: FloatArray = FloatArray(9)
) {

    /**
     * c00, c01, c02
     *
     * <p>
     *
     * c10, c11, c12
     *
     * <p>
     *
     * c20, c21, c22
     */
    constructor(
        c00: Float, c01: Float, c02: Float,
        c10: Float, c11: Float, c12: Float,
        c20: Float, c21: Float, c22: Float,
    ) : this(
        floatArrayOf(
            c00, c01, c02,
            c10, c11, c12,
            c20, c21, c22,
        )
    )

    private operator fun get(ind: Int) = values[ind]
    operator fun get(i: Int, j: Int): Float {
        require(i in 0..2 && j in 0..2)
        return values[i + j * 3]
    }

    private operator fun set(ind: Int, value: Float) {
        values[ind] = value
    }

    operator fun set(i: Int, j: Int, value: Float) {
        require(i in 0..2 && j in 0..2)
        values[i + j * 3] = value
    }

    fun set(
        c00: Float, c01: Float, c02: Float,
        c10: Float, c11: Float, c12: Float,
        c20: Float, c21: Float, c22: Float,
    ) {
        values[0] = c00
        values[1] = c01
        values[2] = c02
        values[3] = c10
        values[4] = c11
        values[5] = c12
        values[6] = c20
        values[7] = c21
        values[8] = c22
    }

    operator fun plus(mat: Matrix3x3): Matrix3x3 {
        val oup = Matrix3x3()
        for (i in 0 until 9) {
            oup[i] = values[i] + mat[i]
        }
        return oup
    }

    operator fun plusAssign(mat: Matrix3x3) {
        for (i in 0 until 9) {
            values[i] += mat[i]
        }
    }

    operator fun minus(mat: Matrix3x3): Matrix3x3 {
        val oup = Matrix3x3()
        for (i in 0 until 9) {
            oup[i] = values[i] - mat[i]
        }
        return oup
    }

    operator fun times(value: Float): Matrix3x3 {
        val oup = Matrix3x3()
        for (i in 0 until 9) {
            oup[i] = values[i] * value
        }
        return oup
    }

    operator fun times(m: Matrix3x3): Matrix3x3 {
        val oup = Matrix3x3()
        for (i in 0..2) {
            for (j in 0..2) {
                for (k in 0..2) {
                    oup.values[i + j * 3] += values[k + j * 3] * m.values[i + k * 3]
                }
            }
        }
        return oup
    }

    fun transpose() {
        val oup = Matrix3x3()
        for (i in 0..2) {
            for (j in 0..2) {
                oup[i + j * 3] = values[j + i * 3]
            }
        }
    }

    fun identity() {
        Arrays.fill(values, 0f)
        for (i in 0..2) {
            values[i + i * 3] = 1f
        }
    }

    override fun toString(): String {
        return "${values[0]}, ${values[1]}, ${values[2]}\n" +
                "${values[3]}, ${values[4]}, ${values[5]}\n" +
                "${values[6]}, ${values[7]}, ${values[8]}\n"
    }

    companion object {
        fun identity() = Matrix3x3(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f,
        )
    }
}
