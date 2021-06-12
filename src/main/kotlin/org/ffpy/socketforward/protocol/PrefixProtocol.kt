package org.ffpy.socketforward.protocol

import io.netty.buffer.ByteBuf
import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.ByteBufUtils

/**
 * 前缀匹配
 */
class PrefixProtocol(override val config: ProtocolConfig) : BaseProtocol(config) {

    private val patternBytes = config.patterns.map { it.toByteArray() }

    override fun match(buf: ByteBuf): Boolean {
        for (pattern in patternBytes) {
            if (buf.readableBytes() < pattern.size) continue
            if (pattern.contentEquals(ByteBufUtils.getBytes(buf, pattern.size))) return true
        }
        return false
    }
}