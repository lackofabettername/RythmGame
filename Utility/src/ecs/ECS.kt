package ecs

import misc.NTuple4
import java.util.*

//https://www.david-colson.com/2020/02/09/making-a-simple-ecs.html
//https://avwie.github.io/building-an-ecs-in-kotlin-mpp

class ECS {
    val Components = HashMap<ComponentKey<*>, ArrayList<Component<*>?>>()
    val Systems = LinkedHashSet<System>()

    private val _entities = ArrayList<Pair<Entity, HashSet<ComponentKey<*>>>>()
    private var _entityID = 0
    private val _freeEntities = LinkedList<Int>()

    val Singleton = Singleton_()

    //region Get Entity Component
    inline operator fun <reified C : Component<C>> get(
        entity: Entity, component: ComponentKey<C>
    ) = Components[component]?.get(entity.ind) as C

    inline operator fun <
            reified C1 : Component<C1>,
            reified C2 : Component<C2>,
            > get(
        entity: Entity,
        component1: ComponentKey<C1>,
        component2: ComponentKey<C2>,
    ) = Pair(
        Components[component1]?.get(entity.ind) as C1,
        Components[component2]?.get(entity.ind) as C2,
    )

    inline operator fun <
            reified C1 : Component<C1>,
            reified C2 : Component<C2>,
            reified C3 : Component<C3>,
            > get(
        entity: Entity,
        component1: ComponentKey<C1>,
        component2: ComponentKey<C2>,
        component3: ComponentKey<C3>,
    ) = Triple(
        Components[component1]?.get(entity.ind) as C1,
        Components[component2]?.get(entity.ind) as C2,
        Components[component3]?.get(entity.ind) as C3,
    )

    inline operator fun <
            reified C1 : Component<C1>,
            reified C2 : Component<C2>,
            reified C3 : Component<C3>,
            reified C4 : Component<C4>,
            > get(
        entity: Entity,
        component1: ComponentKey<C1>,
        component2: ComponentKey<C2>,
        component3: ComponentKey<C3>,
        component4: ComponentKey<C4>,
    ) = NTuple4(
        Components[component1]?.get(entity.ind) as C1,
        Components[component2]?.get(entity.ind) as C2,
        Components[component3]?.get(entity.ind) as C3,
        Components[component4]?.get(entity.ind) as C4,
    )
    //endregion

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

        operator fun contains(component: ComponentKey<*>) = component in _entities[ind].second
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun oldPointer(entity: Entity) = _entities[entity.ind].first != entity


    inner class Singleton_ internal constructor() {
        val Components = HashMap<ComponentKey<*>, Component<*>>()
        val Entity = createEntity()

        inline operator fun <reified C> get(key: ComponentKey<C>) =
            (Components[key] ?: error("There is no $key component")) as C

        operator fun plusAssign(component: Component<*>) {
            Components[component.key] = component
            this@ECS[Entity] += component
        }
    }
}