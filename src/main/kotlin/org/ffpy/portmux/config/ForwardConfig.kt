package org.ffpy.portmux.config

import org.ffpy.portmux.protocol.Protocols
import org.ffpy.portmux.util.AddressUtils
import java.net.SocketAddress

/**
 * 转发配置
 */
class ForwardConfig(val config: Config) {

    /** 转发协议列表 */
    val protocols = config.protocols.map { Protocols.create(it) }

    /** 匹配时需要的最大数据长度 */
    val maxLength = protocols.asSequence().map { it.getMaxLength() }.maxOrNull() ?: 0

    /** 默认转发地址 */
    val defaultAddress: SocketAddress? =
        if (config.default.isEmpty()) null else AddressUtils.parseAddress(config.default)

    /** 连接超时时间 */
    val connectTimeout = config.connectTimeout

    /** 首次读取超时的转发地址 */
    val timeoutAddress: SocketAddress? =
        if (config.readTimeoutAddress.isEmpty()) null else AddressUtils.parseAddress(config.readTimeoutAddress)
}