package org.ffpy.socketforward.protocol

import org.ffpy.socketforward.config.ProtocolConfig
import java.net.SocketAddress

/**
 * 转发匹配协议
 */
interface Protocol {

    /** 对应的转发配置信息 */
    val config: ProtocolConfig

    /** 转发地址 */
    val address: SocketAddress

    /**
     * 是否匹配数据
     *
     * @param data 用于匹配的数据
     * @return true为匹配，false为不匹配
     */
    fun match(data: ByteArray): Boolean

    /**
     * 获取匹配需要获取的最大数据长度
     *
     * @return 匹配需要的最大数据长度
     */
    fun getMaxLength(): Int
}