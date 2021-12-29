package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf

data class MatchEntry(
    val key: ByteBuf,
    var protocol: ProtocolData? = null
) {
    val child: MutableMap<ByteBuf, MatchEntry> = HashMap()

}
