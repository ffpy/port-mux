package org.ffpy.socketforward.protocol

import io.netty.buffer.ByteBuf
import org.ffpy.socketforward.config.ProtocolConfig

/**
 * 转发匹配协议
 */
interface Protocol {

    /** 对应的转发配置信息 */
    val config: ProtocolConfig

    /**
     * 是否匹配数据
     *
     * @return true为匹配，false为不匹配
     */
    fun match(buf: ByteBuf): Boolean
}