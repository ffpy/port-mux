package org.ffpy.portmux.protocol

import io.netty.buffer.Unpooled
import org.ffpy.portmux.config.ProtocolConfig

/**
 * 前缀匹配
 */
class PrefixProtocol(config: ProtocolConfig) : BasePatternProtocol(config, getPatterns(config)) {

    companion object {
        private fun getPatterns(config: ProtocolConfig) =
            config.patterns.asSequence()
                .map { it.toByteArray() }
                .map { Unpooled.wrappedBuffer(it) }
                .toList()
    }

}