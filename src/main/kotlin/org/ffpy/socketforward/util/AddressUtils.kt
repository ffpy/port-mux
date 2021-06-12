package org.ffpy.socketforward.util

import java.net.InetSocketAddress

/**
 * 地址工具类
 */
object AddressUtils {
    /** 地址格式正则 */
    private val addressRegex = Regex("(\\d{1,3}(\\.\\d{1,3}){3})?:\\d{1,5}")

    /**
     * 解析地址字符串
     * 例如 192.168.31.9:8080或:8080
     *
     * @param addr 地址字符串
     * @return 地址
     */
    fun parseAddress(addr: String): InetSocketAddress {
        val split = addr.split(":")

        if (split[0].isEmpty()) return InetSocketAddress(split[1].toInt())
        return InetSocketAddress(split[0], split[1].toInt())
    }

    /**
     * 校验地址格式是否正确
     *
     * @throws IllegalArgumentException 如果地址格式不正确
     * @return true为正确，false为不正确
     */
    fun validAddress(addr: String): Boolean = addr.matches(addressRegex)
}