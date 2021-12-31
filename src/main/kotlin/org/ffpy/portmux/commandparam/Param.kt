package org.ffpy.portmux.commandparam

import com.beust.jcommander.Parameter

/**
 * 命令行参数对象
 */
data class Param(
    @Parameter(names = ["--help"], help = true)
    var help: Boolean = false,

    @Parameter(names = ["-config"], description = "The path of configuration file.")
    var config: String = "config.json5",

    @Parameter(names = ["-watch-config"], description = "Listening for modification of configuration file.")
    var watchConfig: Boolean = true,

    @Parameter(names = ["-epoll"], description = "Whether to use epoll mode. Epoll mode has better performance, but some systems do not support this mode.")
    var epoll: Boolean = false,
)