package ecs

interface ComponentKey<C>
interface Component<C> {
    val key: ComponentKey<C>
}