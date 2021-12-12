package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.util.ArraysUtils

/**
 * 前缀匹配
 */
class PrefixProtocol(config: ProtocolConfig) : BaseProtocol(config) {

    private val patternBytes = config.patterns.map { it.toByteArray() }

    override fun match(data: ByteArray): Boolean = patternBytes.any { ArraysUtils.startWith(data, it) }

    override fun getMaxLength(): Int = patternBytes.asSequence().map { it.size }.maxOrNull() ?: 0
}