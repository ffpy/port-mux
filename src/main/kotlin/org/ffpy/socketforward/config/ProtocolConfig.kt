package org.ffpy.socketforward.config

/**
 * 转发配置
 */
data class ProtocolConfig(
    /** 名称 */
    var name: String = "",
    /** 转发类型 */
    var service: String = "",
    /** 转发地址 */
    var addr: String = "",
    /** 最短字节数，regex类型时使用 */
    var minLen: Int = 0,
    /** 最长字节数，regex类型时使用 */
    var maxLen: Int = 0,
    /** 匹配字符串 */
    var patterns: List<String> = emptyList(),
)