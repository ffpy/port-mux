package org.ffpy.socketforward.protocol

import io.netty.buffer.ByteBuf
import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.ByteBufUtils

/**
 * 字节数组匹配
 */
class BytesProtocol(override val config: ProtocolConfig) : BaseProtocol(config) {

    private val patterns = config.patterns.map { pattern ->
        pattern.split(",").asSequence()
            .map { it.trim() }
            .map { it.toByte() }
            .toCollection(mutableListOf())
            .toByteArray()
    }

    override fun match(buf: ByteBuf): Boolean {
        for (pattern in patterns) {
            if (buf.readableBytes() < pattern.size) continue
            if (pattern.contentEquals(ByteBufUtils.getBytes(buf, pattern.size))) return true
        }
        return false
    }
}