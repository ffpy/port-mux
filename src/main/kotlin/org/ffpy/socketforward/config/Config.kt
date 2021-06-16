package org.ffpy.socketforward.config

/**
 * 项目配置
 */
data class Config(
    /** 监听端口 */
    var listen: String = "",

    /** 默认转发地址 */
    var default: String = "",

    /** 连接超时时间(毫秒) */
    var connectTimeout: Int = 0,

    /** 读取超时时间(毫秒) */
    var readTimeout: Int = 0,

    /** 读取超时的转发地址 */
    var readTimeoutAddress: String = "",

    /** 转发配置 */
    var protocols: List<ProtocolConfig> = emptyList(),
)