package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig

/**
 * 正则匹配
 */
class RegexProtocol(config: ProtocolConfig) : BaseProtocol(config) {

    private val regexes = config.patterns.map { Regex(it) }

    override fun match(data: ByteArray): Boolean {
        if (data.size < config.minLen) return false
        val str = data.toString(Charsets.UTF_8)
        return regexes.any { it.containsMatchIn(str) }
    }

    override fun getMaxLength(): Int = config.maxLen
}