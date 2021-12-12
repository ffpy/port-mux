package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig
import org.ffpy.portmux.util.ArraysUtils

/**
 * 字节数组匹配
 */
class BytesProtocol(config: ProtocolConfig) : BaseProtocol(config) {

    private val patterns = config.patterns.map { pattern ->
        pattern.split(",").asSequence()
            .map { it.trim() }
            .map { it.toByte() }
            .toCollection(mutableListOf())
            .toByteArray()
    }

    override fun match(data: ByteArray): Boolean = patterns.any { ArraysUtils.startWith(data, it) }

    override fun getMaxLength(): Int = patterns.asSequence().map { it.size }.maxOrNull() ?: 0
}