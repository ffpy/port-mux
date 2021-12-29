package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.util.AddressUtils
import java.net.SocketAddress

data class ProtocolData(
    val name: String,
    val address: SocketAddress
) {

    constructor(config: ProtocolConfig) : this(config.name, AddressUtils.parseAddress(config.addr))
}