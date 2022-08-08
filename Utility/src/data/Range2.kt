package data

class Range2(
    var x: IntRange,
    var y: IntRange
) {

    @Suppress("EmptyRange")
    operator fun get(axis: Int) = when (axis % 2) {
        0 -> x
        1 -> y
        else -> 0 until 0
    }

    operator fun set(axis: Int, value: IntRange) {
        when (axis % 2) {
            0 -> x = value
            1 -> y = value
        }
    }

    operator fun iterator() = Iter(this)

    companion object {
        class Iter(val range: Range2) : Iterator<Coord2> {
            val coord: Coord2 = Coord2(range.x.first, range.y.first)

            override fun hasNext() = coord.y <= range.y.last && coord.x <= range.x.last

            override fun next() = coord.copy.also {
                coord.x++
                if (coord.x !in range.x) {
                    coord.y++
                    coord.x = range.x.first
                }
            }

        }
    }
}