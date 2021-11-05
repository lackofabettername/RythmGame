package engine.network

import java.net.InetAddress
import java.io.Serializable
import java.util.Objects

class NetAddress private constructor(
    val AddressType: NetAddressType,
    val Addressable: NetAddressable,
    val Address: InetAddress
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other == null || javaClass != other.javaClass) {
            return false
        }
        val other = other as NetAddress
        return AddressType == other.AddressType && Addressable == other.Addressable && Address == other.Address
    }

    override fun hashCode(): Int {
        return Objects.hash(Address)
    }

    companion object {
        fun localClient(): NetAddress {
            return NetAddress(
                NetAddressType.Loopback,
                NetAddressable.Client,
                InetAddress.getLoopbackAddress()
            )
        }

        fun localServer(): NetAddress {
            return NetAddress(
                NetAddressType.Loopback,
                NetAddressable.Server,
                InetAddress.getLoopbackAddress()
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