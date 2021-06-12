package org.ffpy.socketforward.protocol

import io.netty.buffer.ByteBuf
import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.ByteBufUtils
import kotlin.math.min

/**
 * 正则匹配
 */
class RegexProtocol(override val config: ProtocolConfig) : BaseProtocol(config) {

    private val regexes = config.patterns.map { Regex(it) }

    override fun match(buf: ByteBuf): Boolean {
        for (regex in regexes) {
            val readableBytes = buf.readableBytes()
            if (readableBytes < config.minLen) continue

            val str = ByteBufUtils.getBytes(buf, min(readableBytes, config.maxLen)).toString(Charsets.UTF_8)
            if (regex.containsMatchIn(str)) return true
        }
        return false
    }
}