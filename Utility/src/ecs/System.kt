package ecs

interface System {
    val type : SystemType get() = SystemType.Update
    val keys: Set<ComponentKey<*>>

    fun invoke(ecs: ECS, entity: Entity)
}