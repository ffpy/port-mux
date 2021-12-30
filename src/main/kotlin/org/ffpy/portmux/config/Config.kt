package org.ffpy.portmux.config

import io.netty.util.NettyRuntime

/**
 * 项目配置
 */
data class Config(
    /** 监听端口 */
    var listen: String = "",

    /** 转发服务使用的线程数 */
    var threadNum: Int = NettyRuntime.availableProcessors() * 2,

    /** 日志级别 */
    var logLevel: String = "",

    /** 调试模式 */
    var logDataType: String = "",

    /** 打印转发数据的长度 */
    var logDataLen: Int = Int.MAX_VALUE,

    /** 默认转发地址 */
    var default: String = "",

    /** 连接超时时间(毫秒) */
    var connectTimeout: Int = 0,

    /** 读取超时时间(毫秒) */
    var readTimeout: Int = 0,

    /** 读取超时的转发地址 */
    var readTimeoutAddress: String = "",

    /** 匹配超时时间(毫秒) */
    var matchTimeout: Int = 0,

    /** 转发配置 */
    var protocols: List<ProtocolConfig> = emptyList(),
)