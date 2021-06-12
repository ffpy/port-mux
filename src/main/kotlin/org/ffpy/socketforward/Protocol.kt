package org.ffpy.socketforward

import io.netty.buffer.ByteBuf

enum class Protocols(private val service: String, private val factory: (ProtocolConfig) -> Protocol) {

    PREFIX("prefix", { PrefixProtocol(it) }),
    REGEX("regex", { RegexProtocol(it) }),

    ;

    companion object {
        @Throws(Exception::class)
        fun create(config: ProtocolConfig): Protocol {
            for (item in values()) {
                if (item.service == config.service) {
                    return item.factory(config)
                }
            }
            throw Exception("未知的Protocol: ${config.service}")
        }
    }
}

interface Protocol {

    val config: ProtocolConfig

    fun match(buf: ByteBuf): Boolean
}

/**
 * 前缀匹配
 */
class PrefixProtocol(override val config: ProtocolConfig) : Protocol {

    private val patternBytes = config.patterns.map { it.toByteArray() }

    override fun match(buf: ByteBuf): Boolean {
        for (pattern in patternBytes) {
            if (buf.readableBytes() < pattern.size) continue
            if (pattern.contentEquals(readBytes(buf, pattern.size))) return true
        }
        return false
    }
}

/**
 * 正则匹配
 */
class RegexProtocol(override val config: ProtocolConfig) : Protocol {

    private val regexes = config.patterns.map { Regex(it) }

    override fun match(buf: ByteBuf): Boolean {
        for (regex in regexes) {
            val readableBytes = buf.readableBytes()
            if (readableBytes < config.minLen) continue

            val str = readBytes(buf, config.maxLen).toString(Charsets.UTF_8)
            if (regex.containsMatchIn(str)) return true
        }
        return false
    }
}

private fun readBytes(buf: ByteBuf, size: Int): ByteArray {
    val bytes = ByteArray(size)
    buf.getBytes(0, bytes, 0, size)
    return bytes
}