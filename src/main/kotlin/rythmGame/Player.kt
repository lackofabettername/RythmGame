package rythmGame

import Color
import ColorMode
import engine.application.rendering.Mesh
import util.Vector
import util.Vector2

class Player {
    val mesh: Mesh

    init {
        mesh = Mesh(
            Mesh.Triangles,
            Mesh.StaticDraw,
            intArrayOf(0, 1, 2, 1, 2, 3),
            Vector2.arrayOf(
                0f, 0f,
                40f, 0f,
                0f, 40f,
                40f, 40f,
            ) as Array<Vector>,
            Array(4) { Color(ColorMode.RGB, (it % 2).toFloat(), (it / 2 % 2).toFloat(), 0f) }
        )
    }
}