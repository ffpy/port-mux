package org.ffpy.portmux.protocol

import java.net.SocketAddress

/**
 * 转发匹配协议
 */
interface Protocol {

    /** 名称 */
    val name: String

    /** 转发地址 */
    val address: SocketAddress

    /**
     * 是否匹配数据
     *
     * @param data 用于匹配的数据
     * @return true为匹配，false为不匹配
     */
    fun match(data: MatchData): MatchState
}