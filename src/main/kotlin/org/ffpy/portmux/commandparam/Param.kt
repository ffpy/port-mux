package org.ffpy.portmux.commandparam

import com.beust.jcommander.Parameter

/**
 * 命令行参数对象
 */
data class Param(
    @Parameter(names = ["--help"], help = true)
    var help: Boolean = false,

    @Parameter(names = ["-config"], description = "配置文件路径")
    var config: String = "config.json5",

    @Parameter(names = ["-watch-config"], description = "是否监听配置文件改变")
    var watchConfig: Boolean = true,

    @Parameter(names = ["-epoll"], description = "是否使用epoll模式，epoll模式的性能更好，但是有的系统不支持这个模式")
    var epoll: Boolean = false,
)