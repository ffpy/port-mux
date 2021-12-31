package org.ffpy.portmux.matcher

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.ffpy.portmux.protocol.Protocol
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
                    log.info("{} match protocol: {}", remoteAddress, protocol.name)
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
            log.info("{} match fail, forward to default address", remoteAddress)
            return MatchResult(true, defaultAddress)
        }

        protocols = maybeList

        return MatchResult(false, null)
    }
}