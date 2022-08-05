package ecs

interface System {
    val type : SystemType
    val keys: Set<ComponentKey<*>>

    fun invoke(ecs: ECS, entity: Entity)
}