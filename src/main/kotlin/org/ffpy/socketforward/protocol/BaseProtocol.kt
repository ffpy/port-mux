package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.AddressUtils
import java.net.SocketAddress

abstract class BaseProtocol(override val config: ProtocolConfig) : Protocol {
    override val address: SocketAddress by lazy {
        AddressUtils.parseAddress(config.addr)
    }
}