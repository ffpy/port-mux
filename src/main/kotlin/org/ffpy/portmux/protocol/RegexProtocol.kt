package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.matcher.MatchData
import org.ffpy.portmux.matcher.MatchState
import kotlin.math.min

/**
 * 正则匹配
 */
class RegexProtocol(config: ProtocolConfig) : BaseProtocol(config) {

    private val regexes = config.patterns.map { Regex(it) }

    override fun match(data: MatchData): MatchState {
        val buf = data.buf
        if (buf.readableBytes() < config.minLen) return MatchState.NOT_MATCH

        val len = min(buf.readableBytes(), config.maxLen)
        val str = buf.toString(buf.readerIndex(), len, Charsets.UTF_8)

        val match = regexes.any { it.containsMatchIn(str) }
        return if (match) MatchState.MATCH else MatchState.NOT_MATCH
    }
}