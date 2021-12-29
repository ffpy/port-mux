package org.ffpy.portmux.config

/**
 * 协议配置
 */
data class ProtocolConfig(
    /** 名称 */
    var name: String = "",

    /** 转发类型 */
    var type: String = "",

    /** 转发地址 */
    var addr: String = "",

    /** 匹配字符串 */
    var patterns: List<String> = emptyList(),
)