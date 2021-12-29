package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

data class MatchData(val buf: ByteBuf) {
    val string: String by lazy { buf.toString(StandardCharsets.UTF_8) }
}
