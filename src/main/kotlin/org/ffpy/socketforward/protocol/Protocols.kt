package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig

enum class Protocols(private val service: String, private val factory: (ProtocolConfig) -> Protocol) {

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
                if (item.service == config.service) {
                    return item.factory(config)
                }
            }
            throw Exception("未知的Protocol: ${config.service}")
        }
    }
}