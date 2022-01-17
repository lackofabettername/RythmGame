package util

object GeometryMath {
    /**
     * @return the point of intersection of null if there is none
     * @see <a href="https://stackoverflow.com/a/1968345/16426346">Stackoverflow</a>
     */
    fun lineIntersection(
        p0: Vector2, p1: Vector2,
        p2: Vector2, p3: Vector2
    ): Vector2? {

        val s1 = Vector2(
            p1.x - p0.x,
            p1.y - p0.y
        )
        val s2 = Vector2(
            p3.x - p2.x,
            p3.y - p2.y
        )

        val s: Float = (-s1.y * (p0.x - p2.x) + s1.x * (p0.y - p2.y)) / (-s2.x * s1.y + s1.x * s2.y)
        val t: Float = (s2.x * (p0.y - p2.y) - s2.y * (p0.x - p2.x)) / (-s2.x * s1.y + s1.x * s2.y)

        //return if (s in 0.0..1.0 && t in 0.0..1.0) {
        return if (0.0 < s && s < 1.0 && 0.0 < t && t < 1.0) {
            Vector2(p0.x + t * s1.x, p0.y + t * s1.y)
        } else
            null
    }
}