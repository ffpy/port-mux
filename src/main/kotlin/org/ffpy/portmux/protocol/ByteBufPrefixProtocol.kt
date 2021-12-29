package org.ffpy.portmux.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.matcher.MatchData
import org.ffpy.portmux.matcher.MatchState
import kotlin.math.min

/**
 * 基于ByteBuf的前缀匹配
 */
abstract class ByteBufPrefixProtocol(
    config: ProtocolConfig,
    private val patterns: List<ByteBuf>
) : BaseProtocol(config) {

    override fun match(data: MatchData): MatchState {
        val buf = data.buf
        var state = MatchState.NOT_MATCH
        for (pattern in patterns) {
            val matchLength = min(buf.readableBytes(), pattern.readableBytes())
            if (ByteBufUtil.equals(
                    buf, buf.readerIndex(),
                    pattern, pattern.readerIndex(),
                    matchLength
                )
            ) {
                if (matchLength == pattern.readableBytes()) {
                    return MatchState.MATCH
                } else {
                    state = MatchState.MAYBE
                }
            }
        }
        return state
    }
}