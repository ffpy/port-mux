package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.AddressUtils
import java.net.SocketAddress

abstract class BaseProtocol(val config: ProtocolConfig) : Protocol {

    override val name: String
        get() = config.name

    override val address: SocketAddress by lazy {
        AddressUtils.parseAddress(config.addr)
    }
}