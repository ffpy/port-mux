package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import org.ffpy.portmux.config.ProtocolConfig

/**
 * 十六进制数据匹配
 */
class HexProtocol(config: ProtocolConfig) : BasePatternProtocol(config, getPatterns(config)) {

    companion object {
        private fun getPatterns(config: ProtocolConfig) = config.patterns.asSequence()
            .map { ByteBufUtil.decodeHexDump(it) }
            .map { Unpooled.wrappedBuffer(it) }
            .toList()
    }
}