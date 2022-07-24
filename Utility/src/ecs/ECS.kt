package ecs

import java.util.*

//https://www.david-colson.com/2020/02/09/making-a-simple-ecs.html
//https://avwie.github.io/building-an-ecs-in-kotlin-mpp

class ECS {
    val Components = HashMap<ComponentKey<*>, ArrayList<Component<*>?>>()
    val Systems = HashSet<System>()

    private val _entities = ArrayList<Pair<Entity, HashSet<ComponentKey<*>>>>()
    private var _entityID = 0
    private val _freeEntities = LinkedList<Int>()

    val Singleton = Singleton_()

    inline operator fun <reified C : Component<C>> get(
        entity: Entity, componentType: ComponentKey<C>
    ) = Components[componentType]?.get(entity.ind) as C

    fun update(type: SystemType) {
        for (system in Systems.filter { it.type == type }) {
            for (i in 0 until _entities.size) {
                val (entity, components) = _entities[i]
                if (system.keys.all { it in components }) {
                    system.invoke(this, entity)
                }
            }
        }
    }

    fun createEntity(): Entity {
        return if (_freeEntities.isEmpty()) {
            val entity = Entity(_entities.size, _entityID++)
            _entities += entity to HashSet()
            entity
        } else {
            val entity = Entity(_freeEntities.poll(), _entityID++)
            _entities[entity.ind] = entity to HashSet()
            entity
        }
    }

    fun removeEntity(entity: Entity) {
        if (oldPointer(entity)) return

        for (componentType in _entities[entity.ind].second) {
            Components[componentType]!![entity.ind] = null
        }
        _entities[entity.ind].second.clear()
        _freeEntities += entity.ind
    }

    operator fun get(vararg requirements: ComponentKey<*>) = _entities.asSequence()
        .filter { (_, modules) ->
            requirements.all { it in modules }
        }
        .map { it.first }

    operator fun get(entity: Entity) = if (oldPointer(entity))
        error("This entity has been removed")
    else
        ComponentsDelegate(entity.ind)

    inner class ComponentsDelegate(val ind: Int) {
        operator fun plusAssign(component: Component<*>) {
            if (component.key !in Components)
                Components[component.key] = ArrayList()

            val components = Components[component.key]!!

            while (ind >= components.size)
                components.add(null)

            components[ind] = component
            _entities[ind].second += component.key
        }

        operator fun minusAssign(component: ComponentKey<*>) {
            if (component !in _entities[ind].second) return

            val components = Components[component] ?: return

            components[ind] = null
            _entities[ind].second -= component
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun oldPointer(entity: Entity) = _entities[entity.ind].first != entity


    inner class Singleton_ internal constructor() {
        val Components = HashMap<ComponentKey<*>, Component<*>>()

        inline operator fun <reified C> get(key: ComponentKey<C>) =
            (Components[key] ?: error("There is no $key component")) as C

        operator fun plusAssign(component: Component<*>) {
            Components[component.key] = component
        }
    }
}