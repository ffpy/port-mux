package org.ffpy.socketforward

import org.ffpy.socketforward.util.JsonUtils
import java.nio.file.Path

/**
 * 读取配置文件
 *
 * @param path 文件路径
 * @return 配置对象，null代表找不到配置文件
 */
fun getConfig(path: Path): Config? {
    if (!path.toFile().exists()) {
        log.error("找不到配置文件")
        return null
    }
    return JsonUtils.parse(path, Config::class.java)
}

/**
 * 项目配置
 */
data class Config(
    /** 监听端口 */
    var listen: String = "",
    /** 默认转发地址 */
    var default: String = "",
    /** 读取超时时间(毫秒) */
    var readTimeout: Int = 0,
    /** 连接超时时间(毫秒) */
    var connectTimeout: Int = 0,
    /** 转发配置 */
    var protocols: List<Protocol> = emptyList(),
)

/**
 * 转发配置
 */
data class Protocol(
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