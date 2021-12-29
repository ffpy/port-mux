package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.config.ProtocolConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.SocketAddress

class FastMatcher {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(FastMatcher::class.java)

        private val root: MatchEntry = initRoot(Configs.config.protocols)

        private fun initRoot(protocols: List<ProtocolConfig>): MatchEntry {
            val rootEntry = MatchEntry(Unpooled.EMPTY_BUFFER)

            for (protocol in protocols) {
                if (protocol.type == "hex") {
                    processHexProtocol(protocol, rootEntry)
                }
                if (protocol.type == "prefix") {
                    processPrefixProtocol(protocol, rootEntry)
                }
            }

            return rootEntry
        }

        private fun processHexProtocol(protocol: ProtocolConfig, rootEntry: MatchEntry) {
            val protocolData = ProtocolData(protocol)

            for (pattern in protocol.patterns) {
                val hex = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(pattern))
                val leaf = buildTree(rootEntry, hex, 0)
                leaf.protocol = protocolData
            }
        }

        private fun processPrefixProtocol(protocol: ProtocolConfig, rootEntry: MatchEntry) {
            val protocolData = ProtocolData(protocol)

            for (pattern in protocol.patterns) {
                val hex = Unpooled.wrappedBuffer(pattern.toByteArray(CharsetUtil.UTF_8))
                val leaf = buildTree(rootEntry, hex, 0)
                leaf.protocol = protocolData
            }
        }

        private fun buildTree(layer: MatchEntry, hex: ByteBuf, index: Int): MatchEntry {
            val node = hex.slice(index, 1)
            val entry = layer.child.computeIfAbsent(node) { MatchEntry(it) }

            if (index + 1 == hex.readableBytes()) {
                return entry
            }

            return buildTree(entry, hex, index + 1)
        }
    }

    private var currentLayer: MatchEntry = root
    private var currentIndex = -1

    fun match(buf: ByteBuf, remoteAddress: SocketAddress?, defaultAddress: SocketAddress?): MatchResult {
        if (currentIndex == -1) currentIndex = buf.readerIndex()

        val len = buf.readableBytes()
        for (i in 0 until len) {
            val node = buf.slice(currentIndex++, 1)
            currentLayer = currentLayer.child[node] ?: return MatchResult(true, null)
            if (currentLayer.protocol != null) break
        }

        val protocol = currentLayer.protocol
        if (protocol != null) {
            log.info("{}匹配协议: {}", remoteAddress, protocol.name)
        } else {
            log.info("{}匹配失败，转发到默认地址", remoteAddress)
        }

        return MatchResult(true, protocol?.address ?: defaultAddress)
    }
}