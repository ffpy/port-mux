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

    @Parameter(names = ["-debug"], description = "启用调试模式，打印转发数据，可选值: string, byte")
    var debug: String = "",
)