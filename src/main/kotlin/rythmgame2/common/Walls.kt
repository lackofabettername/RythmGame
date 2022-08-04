package rythmgame2.common

import Color
import ecs.ECS
import ecs.Entity
import engine.application.rendering.Mesh
import rythmgame2.common.ShadowMeshComp.Companion.getShadowMesh
import util.Vector
import util.Vector2

fun createWalls(
    ecs: ECS
): Entity {
    val walls = ecs.createEntity()

    val mesh = Mesh(
        Mesh.Triangles,
        Mesh.StaticDraw,
        intArrayOf(
            0, 1, 2, 1, 2, 3,
            2, 3, 4, 2, 4, 5, 4, 5, 6, 5, 6, 7,
            6, 7, 8, 6, 8, 9,
            10, 11, 12, 11, 12, 13,
            14, 15, 16, 15, 16, 17,
        ),
        Vector2.arrayOf(
            0f, 400f,
            20f, 400f,
            0f, 100f,
            20f, 100f,

            50f, 50f,
            35.86f, 35.86f,

            100f, 20f,
            100f, 0f,
            800f, 0f,
            800f, 20f,

            300f, 150f,
            300f, 170f,
            320f, 150f,
            320f, 170f,

            340f, 150f,
            340f, 170f,
            360f, 150f,
            360f, 170f,
        ) as Array<Vector>,
        Array(18) { Color.rgb(0.0f, 0.0f, 0.0f) } as Array<Vector>
    )

    ecs[walls] += TransformComp(
        Vector2(150f, 80f),
    )
    ecs[walls] += RenderComp(mesh, 10, 5, null)
    ecs[walls] += getShadowMesh(mesh)

    return walls
}