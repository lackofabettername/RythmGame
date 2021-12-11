package engine.network.common

import java.util.function.BiConsumer

typealias MessageConsumer = BiConsumer<NetAddress, NetMessage>