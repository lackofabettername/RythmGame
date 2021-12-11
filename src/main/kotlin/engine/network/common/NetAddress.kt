package engine.network.common

import java.net.InetAddress
import java.io.Serializable

data class NetAddress private constructor(
    val AddressType: NetAddressType,
    val Addressable: NetAddressable,
    val Address: InetAddress
) : Serializable {

    companion object {
        val loopbackClient by lazy {
            NetAddress(
                NetAddressType.Loopback,
                NetAddressable.Client,
                InetAddress.getLoopbackAddress()
            )
        }

        val loopbackServer by lazy {
            NetAddress(
                NetAddressType.Loopback,
                NetAddressable.Server,
                InetAddress.getLoopbackAddress()
            )
        }

        val localClient by lazy {
            NetAddress(
                NetAddressType.Internet,
                NetAddressable.Client,
                InetAddress.getLocalHost()
            )
        }

        val localServer by lazy {
            NetAddress(
                NetAddressType.Internet,
                NetAddressable.Server,
                InetAddress.getLocalHost()
            )
        }

        fun remoteClient(address: InetAddress): NetAddress {
            return NetAddress(
                NetAddressType.Internet,
                NetAddressable.Client,
                address
            )
        }

        fun remoteServer(address: InetAddress): NetAddress {
            return NetAddress(
                NetAddressType.Internet,
                NetAddressable.Server,
                address
            )
        }
    }
}