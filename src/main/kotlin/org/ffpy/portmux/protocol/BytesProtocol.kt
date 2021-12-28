package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import org.ffpy.portmux.config.ProtocolConfig

/**
 * 字节数组匹配
 */
class BytesProtocol(config: ProtocolConfig) : BasePatternProtocol(config, getPatterns(config)) {

    companion object {
        private fun getPatterns(config: ProtocolConfig) = config.patterns.asSequence()
            .map { ByteBufUtil.decodeHexDump(it) }
            .map { Unpooled.wrappedBuffer(it) }
            .map { Unpooled.wrappedUnmodifiableBuffer(it) }
            .toList()
    }
}