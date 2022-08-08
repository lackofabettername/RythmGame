package data

typealias Dim2 = Coord2

inline operator fun Dim2.contains(coord: Coord2) =
    coord.x >= 0 && coord.y >= 0 && coord.x < x && coord.y < y

inline operator fun Dim2.contains(coord: Pair<Int, Int>) =
    coord.first >= 0 && coord.second >= 0 && coord.first < x && coord.second < y

operator fun Dim2.iterator() = Dim2Iter(this)

class Dim2Iter(private val range: Dim2) : Iterator<Coord2> {
    private val coord: Coord2 = Coord2()

    override fun hasNext() = coord.y < range.y && coord.x < range.x

    override fun next() = coord.copy.also {
        coord.x++
        if (coord.x >= range.x) {
            coord.x = 0
            coord.y++
        }
    }

}