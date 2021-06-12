package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig

enum class Protocols(val type: String, private val factory: (ProtocolConfig) -> Protocol) {

    PREFIX("prefix", { PrefixProtocol(it) }),

    REGEX("regex", { RegexProtocol(it) }),

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