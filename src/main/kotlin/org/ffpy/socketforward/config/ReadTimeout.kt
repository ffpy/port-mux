package org.ffpy.socketforward.config

data class ReadTimeout(
    /** 超时时间 */
    var timeout: Int = 0,
    /** 超时的转发地址 */
    var address: String = "",
)
