package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig
import org.ffpy.socketforward.util.ArraysUtils

/**
 * 前缀匹配
 */
class PrefixProtocol(override val config: ProtocolConfig) : BaseProtocol(config) {

    private val patternBytes = config.patterns.map { it.toByteArray() }

    override fun match(data: ByteArray): Boolean = patternBytes.any { ArraysUtils.startWith(data, it) }

    override fun getMaxLength(): Int = patternBytes.asSequence().map { it.size }.maxOrNull() ?: 0
}