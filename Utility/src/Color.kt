import util.Vector
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Stores colors in either RGB, HSV or HSL
 */
@Suppress("unused")
class Color(//TODO lerp, mix, setters
    private var _mode: ColorMode,
    v0: Float,
    v1: Float,
    v2: Float,
    alpha: Float = 1f
) : Vector, Serializable {
    private var _v0 = v0
    private var _v1 = v1
    private var _v2 = v2

    var alpha = alpha
        get() = field / range
        set(value) {
            field = value / range
        }


    var range = 1f

    override fun getDimension(): Int {
        return 4
    }

    override fun get(axis: Int): Float {
        return when (axis) {
            0 -> _v0
            1 -> _v1
            2 -> _v2
            3 -> alpha
            else -> -1f
        }
    }

    //region RGB

    private inline fun HSVtoRGB(func: (h: Int, c: Float, x: Float) -> Float): Float {
        val c = _v1 * _v2 // Saturation * Value
        val h = _v0 / (60 / 360f) // Hue / 6
        val x = c * (1 - abs(h % 2 - 1))
        val m = _v2 - c // Value - c

        return if (!java.lang.Float.isFinite(h)) {
            0f
        } else {
            func(ceil(h).toInt(), c, x) + m
        }
    }

    private inline fun HSLtoRGB(func: (h: Int, c: Float, x: Float) -> Float): Float {
        val c = (1 - abs(2 * _v2 - 1)) * _v1
        val h = _v0 / (60 / 360f)
        val x = c * (1 - abs(h % 2 - 1))
        val m = _v2 - c / 2

        return if (!java.lang.Float.isFinite(h)) {
            0f
        } else {
            func(ceil(h).toInt(), c, x) + m
        }
    }

    var red: Float
        get() = when (_mode) {
            ColorMode.RGB -> _v0

            ColorMode.HSV -> {
                HSVtoRGB { h, c, x ->
                    when (h) {
                        6, 1, 0 -> c
                        5, 2 -> x
                        else -> 0f
                    }
                }
            }
            ColorMode.HSL -> {
                HSLtoRGB { h, c, x ->
                    when (h) {
                        5, 0 -> c
                        4, 1 -> x
                        else -> 0f
                    }
                }
            }
        } * range
        set(red) {
            _v0 = when (_mode) {
                ColorMode.RGB -> red / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }

    var green: Float
        get() = when (_mode) {
            ColorMode.RGB -> _v1
            ColorMode.HSV -> {
                HSVtoRGB { h, c, x ->
                    when (h) {
                        4, 1, 0 -> x
                        3, 2 -> c
                        else -> 0f
                    }
                }
            }
            ColorMode.HSL -> {
                HSLtoRGB { h, c, x ->
                    when (h) {
                        3, 0 -> x
                        2, 1 -> c
                        else -> 0f
                    }
                }
            }
        } * range
        set(green) {
            _v1 = when (_mode) {
                ColorMode.RGB -> green / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }

    var blue: Float
        get() = when (_mode) {
            ColorMode.RGB -> _v2
            ColorMode.HSV -> {
                HSVtoRGB { h, c, x ->
                    when (h) {
                        6, 3 -> x
                        5, 4 -> c
                        else -> 0f
                    }
                }
            }
            ColorMode.HSL -> {
                HSLtoRGB { h, c, x ->
                    when (h) {
                        5, 2 -> x
                        4, 3 -> c
                        else -> 0f
                    }
                }
            }
        } * range
        set(blue) {
            _v2 = when (_mode) {
                ColorMode.RGB -> blue / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }
    //endregion

    //region HSV/HSL
    var hue: Float
        get() = when (_mode) {
            ColorMode.HSV, ColorMode.HSL -> _v0
            ColorMode.RGB -> {
                val cMax = max(max(_v0, _v1), _v2)
                val cMin = min(min(_v0, _v1), _v2)
                val delta = (cMax - cMin) * 6
                when (cMax) {
                    _v0 -> ((_v1 - _v2) / delta).mod(1f)
                    _v1 -> (_v2 - _v0) / delta + 2 / 6f
                    _v2 -> (_v0 - _v1) / delta + 4 / 6f
                    else -> 0f
                }
            }
        } * range
        set(hue) {
            _v0 = when (_mode) {
                ColorMode.HSV, ColorMode.HSL -> hue / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }
    var saturationHSV: Float
        get() = when (_mode) {
            ColorMode.HSV -> _v1
            ColorMode.HSL -> {
                val v = value
                if (v == 0f)
                    0f
                else
                    2 * (1 - lightness / v)
            }
            ColorMode.RGB -> {
                val cMax = max(max(_v0, _v1), _v2)
                val cMin = min(min(_v0, _v1), _v2)
                val delta = cMax - cMin
                if (cMax == 0f) 0f else delta / cMax
            }
        } * range
        set(saturation) {
            _v1 = when (_mode) {
                ColorMode.HSV -> saturation / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }
    var saturationHSL: Float
        get() = when (_mode) {
            ColorMode.HSL -> _v1
            ColorMode.HSV -> {
                val l = lightness
                if (l == 0f || l == 1f)
                    0f
                else
                    (value - l) / Math.min(l, 1 - l)
            }
            ColorMode.RGB -> {
                val cMax = max(max(_v0, _v1), _v2)
                val cMin = min(min(_v0, _v1), _v2)
                val delta = cMax - cMin
                if (cMax == 0f) 0f else delta / (1 - Math.abs(cMax + cMin - 1))
            }
        } * range
        set(saturation) {
            _v1 = when (_mode) {
                ColorMode.HSL -> saturation / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }
    var value: Float
        get() = when (_mode) {
            ColorMode.HSV -> _v2
            ColorMode.HSL -> {
                val l = lightness
                if (l == 0f || l == 1f)
                    0f
                else
                    l + saturationHSL * Math.min(l, 1 - l)
            }
            ColorMode.RGB -> max(max(_v0, _v1), _v2)
        } * range
        set(value) {
            _v2 = when (_mode) {
                ColorMode.HSV -> value / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }
    var lightness: Float
        get() = when (_mode) {
            ColorMode.HSL -> _v2
            ColorMode.HSV -> value * (1 - value / 2)
            ColorMode.RGB -> (max(max(_v0, _v1), _v2) + min(min(_v0, _v1), _v2)) / 2
        } * range
        set(lightness) {
            _v2 = when (_mode) {
                ColorMode.HSL -> lightness / range
                else -> throw UnsupportedOperationException("TODO")
            }
        }

    //endregion

    fun lerp(t: Float, col: Color) {
        val temp = range to col.range
        range = 1f
        col.range = 1f

        when (_mode) {
            //@formatter:off
            ColorMode.RGB -> {
                _v0 += t * (col.red           - _v0)
                _v1 += t * (col.green         - _v1)
                _v2 += t * (col.blue          - _v2)
            }
            ColorMode.HSV -> {
                _v0 += t * (col.hue           - _v0)
                _v1 += t * (col.saturationHSV - _v1)
                _v2 += t * (col.value         - _v2)
            }
            ColorMode.HSL -> {
                _v0 += t * (col.hue           - _v0)
                _v1 += t * (col.saturationHSL - _v1)
                _v2 += t * (col.lightness     - _v2)
            }
            //@formatter:on
        }
        alpha += t * (col.alpha - alpha)

        range = temp.first
        col.range = temp.second
    }

    fun copy() = Color(_mode, _v0, _v1, _v2, alpha)

    companion object {
        fun lerp(t: Float, a: Color, b: Color): Color {
            val color = a.copy()
            color.lerp(t, b)
            return color
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val col = Color(ColorMode.HSV, 234f / 360, 0.5f, 0.5f)
            println(col.red * 255)
            println(col.green * 255)
            println(col.blue * 255)
        }
    }
}