package space

class Matrix4x4(
    val values: FloatArray = FloatArray(16)
) {

    /**
     * c00, c01, c02, c03,
     *
     * <p>
     *
     * c10, c11, c12, c13,
     *
     * <p>
     *
     * c20, c21, c22, c23,
     *
     * <p>
     *
     * c30, c31, c32, c33
     */
    constructor(
        c00: Float, c01: Float, c02: Float, c03: Float,
        c10: Float, c11: Float, c12: Float, c13: Float,
        c20: Float, c21: Float, c22: Float, c23: Float,
        c30: Float, c31: Float, c32: Float, c33: Float
    ) : this(
        floatArrayOf(
            c00, c01, c02, c03,
            c10, c11, c12, c13,
            c20, c21, c22, c23,
            c30, c31, c32, c33
        )
    )

    private operator fun get(ind: Int) = values[ind]
    operator fun get(i: Int, j: Int): Float {
        require(i in 0..3 && j in 0..3)
        return values[i + j * 4]
    }

    private operator fun set(ind: Int, value: Float) {
        values[ind] = value
    }

    operator fun set(i: Int, j: Int, value: Float) {
        require(i in 0..3 && j in 0..3)
        values[i + j * 4] = value
    }

    operator fun plus(mat: Matrix4x4): Matrix4x4 {
        val oup = Matrix4x4()
        for (i in 0 until 16) {
            oup[i] = values[i] + mat[i]
        }
        return oup
    }

    operator fun minus(mat: Matrix4x4): Matrix4x4 {
        val oup = Matrix4x4()
        for (i in 0 until 16) {
            oup[i] = values[i] - mat[i]
        }
        return oup
    }

    operator fun times(value: Float): Matrix4x4 {
        val oup = Matrix4x4()
        for (i in 0 until 16) {
            oup[i] = values[i] * value
        }
        return oup
    }

    operator fun times(m: Matrix4x4): Matrix4x4 {
        val oup = Matrix4x4()
        for (i in 0..3) {
            for (j in 0..3) {
                for (k in 0..3) {
                    oup.values[i + j * 4] += values[k + j * 4] * m.values[i + k * 4]
                }
            }
        }
        return oup
    }

    fun transpose() {
        val oup = Matrix4x4()
        for (i in 0..3) {
            for (j in 0..3) {
                oup[i + j * 4] = values[j + i * 4]
            }
        }
    }

    override fun toString(): String {
        return "${values[0]}, ${values[1]}, ${values[2]}, ${values[3]}\n" +
                "${values[4]}, ${values[5]}, ${values[6]}, ${values[7]}\n" +
                "${values[8]}, ${values[9]}, ${values[10]}, ${values[11]}\n" +
                "${values[12]}, ${values[13]}, ${values[14]}, ${values[15]}"
    }
}
