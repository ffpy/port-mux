package org.ffpy.portmux.protocol

import org.ffpy.portmux.config.ProtocolConfig

enum class Protocols(val type: String, private val factory: (ProtocolConfig) -> Protocol) {

    PREFIX("prefix", { PrefixProtocol(it) }),

    REGEX("regex", { RegexProtocol(it) }) {
        override fun check(config: ProtocolConfig, index: Int) {
            if (config.minLen <= 0) throw Exception("protocol[${index}].min_len必须大于0")
            if (config.maxLen <= 0) throw Exception("protocol[${index}].max_len必须大于0")
        }
    },

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

    /**
     * 检查配置文件
     * @throws Exception 如果配置文件有问题
     */
    open fun check(config: ProtocolConfig, index: Int) {}
}