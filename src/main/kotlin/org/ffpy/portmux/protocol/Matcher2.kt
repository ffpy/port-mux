package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.config.ProtocolConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.SocketAddress

class Matcher2 {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Matcher2::class.java)

        private val matchMap: Map<ByteBuf, ProtocolData> = initMatchMap(Configs.config.protocols)

        private fun initMatchMap(protocols: List<ProtocolConfig>): Map<ByteBuf, ProtocolData> {
            val map: MutableMap<ByteBuf, ProtocolData> = HashMap()
            for (protocol in protocols) {
                when (protocol.type) {
                    "prefix" -> addToMap(protocol, map) { Unpooled.wrappedBuffer(it.toByteArray(Charsets.UTF_8)) }
                    "hex" -> addToMap(protocol, map) { Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(it)) }
                    else -> throw RuntimeException("未知的协议类型: ${protocol.type}")
                }
            }
            return map
        }

        private fun addToMap(
            protocol: ProtocolConfig,
            map: MutableMap<ByteBuf, ProtocolData>,
            bufMapper: (String) -> ByteBuf
        ) {
            for (pattern in protocol.patterns) {
                val buf = bufMapper(pattern)
                val old = map.put(buf, ProtocolData(protocol))
                if (old != null) {
                    throw RuntimeException("${protocol.name} 与 ${old.name} 存在重复匹配项")
                }
            }
        }
    }

    fun match(buf: ByteBuf, remoteAddress: SocketAddress?, defaultAddress: SocketAddress?): MatchResult {
        
    }
}