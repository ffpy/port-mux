package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.slf4j.LoggerFactory
import java.net.SocketAddress

class Matcher(
    private var protocols: List<Protocol>,
) {

    companion object {
        private val log = LoggerFactory.getLogger(Matcher::class.java)
    }

    fun match(buf: ByteBuf, remoteAddress: SocketAddress?, defaultAddress: SocketAddress?): MatchResult {
        val data = MatchData(Unpooled.wrappedUnmodifiableBuffer(buf))

        val maybeList = ArrayList<Protocol>()
        for (protocol in protocols) {
            when (protocol.match(data)) {
                MatchState.MATCH -> {
                    log.info("{}匹配协议: {}", remoteAddress, protocol.name)
                    return MatchResult(true, protocol.address)
                }
                MatchState.MAYBE -> {
                    maybeList.add(protocol)
                }
                MatchState.NOT_MATCH -> {
                    // do nothing
                }
            }
        }

        if (maybeList.isEmpty()) {
            log.info("{}匹配失败，转发到默认地址", remoteAddress)
            return MatchResult(true, defaultAddress)
        }

        protocols = maybeList

        return MatchResult(false, null)
    }
}