package org.ffpy.portmux.matcher

import java.net.SocketAddress

/**
 * 匹配结果
 */
data class MatchResult(
    /** 是否已经匹配结束 */
    val finish: Boolean,

    /** 匹配结果地址，为空则说明匹配失败 */
    val address: SocketAddress?
)
