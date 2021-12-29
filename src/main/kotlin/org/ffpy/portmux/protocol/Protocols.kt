package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig

enum class Protocols(val type: String, private val factory: (ProtocolConfig) -> Protocol) {

    PREFIX("prefix", { PrefixProtocol(it) }),

    REGEX("regex", { RegexProtocol(it) }),

    BYTES("bytes", { BytesProtocol(it) }),

    HEX("hex", { HexProtocol(it) }),

    ;

    companion object {

        /**
         * 根据转发配置生成对应的转发匹配对象
         */
        @Throws(Exception::class)
        fun create(config: ProtocolConfig): Protocol {
            for (item in values()) {
                if (item.type == config.type) {
                    return item.factory(config)
                }
            }
            throw Exception("未知的Protocol: ${config.type}")
        }
    }
}