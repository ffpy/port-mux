package org.ffpy.portmux.protocol

import io.netty.buffer.Unpooled
import org.ffpy.portmux.config.ProtocolConfig

/**
 * 字节数组匹配
 */
class BytesProtocol(config: ProtocolConfig) : BasePatternProtocol(config, getPatterns(config)) {

    companion object {
        private fun getPatterns(config: ProtocolConfig) = config.patterns.asSequence()
            .map { pattern ->
                pattern.split(",").asSequence()
                    .map { it.trim() }
                    .map { it.toByte() }
                    .toList()
                    .toByteArray()
            }
            .map { Unpooled.wrappedBuffer(it) }
            .toList()
    }
}