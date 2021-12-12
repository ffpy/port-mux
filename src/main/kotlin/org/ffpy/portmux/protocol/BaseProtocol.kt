package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.util.AddressUtils
import java.net.SocketAddress

abstract class BaseProtocol(val config: ProtocolConfig) : Protocol {

    override val name: String
        get() = config.name

    override val address: SocketAddress by lazy {
        AddressUtils.parseAddress(config.addr)
    }
}