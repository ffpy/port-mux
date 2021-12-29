package org.ffpy.portmux.config

import org.ffpy.portmux.protocol.Protocols
import org.ffpy.portmux.util.AddressUtils
import java.net.SocketAddress

/**
 * 转发配置
 */
class ForwardConfig(config: Config) {

    /** 转发协议列表 */
    val protocols = config.protocols.map { Protocols.create(it) }

    /** 默认转发地址 */
    val defaultAddress: SocketAddress? =
        if (config.default.isEmpty()) null else AddressUtils.parseAddress(config.default)

    /** 连接超时时间(毫秒) */
    val connectTimeout = config.connectTimeout

    /** 读取超时时间(毫秒) */
    val readTimeout = config.readTimeout

    /** 匹配超时时间(毫秒) */
    val matchTimeout = config.matchTimeout

    /** 读取超时的转发地址 */
    val readTimeoutAddress: SocketAddress? =
        if (config.readTimeoutAddress.isEmpty()) null else AddressUtils.parseAddress(config.readTimeoutAddress)
}