package data

class Map2<T>(
    val map: Array<Array<T>>
) {
    inline val indices get() = Dim2(map.size, map[0].size)

    operator fun get(ind: Coord2) = map[ind.x][ind.y]
    operator fun get(x: Int, y: Int) = map[x][y]
    inline fun getOrElse(ind: Coord2, default: T) = if (ind in indices) get(ind) else default
    inline fun getOrElse(x: Int, y: Int, default: T) = if (x to y in indices) get(x, y) else default

    operator fun set(ind: Coord2, value: T) {
        map[ind.x][ind.y] = value
    }

    operator fun set(x: Int, y: Int, value: T) {
        map[x][y] = value
    }

    companion object {
        inline fun <reified T> map2(dimX: Int, dimY: Int, init: (Pair<Int, Int>) -> T) =
            Map2(
                Array(dimX) { x ->
                    Array(dimY) { y ->
                        init(x to y)
                    }
                }
            )

        inline fun <reified T> map2(dim: Dim2, init: (Pair<Int, Int>) -> T) =
            Map2(
                Array(dim.x) { x ->
                    Array(dim.y) { y ->
                        init(x to y)
                    }
                }
            )
    }
}