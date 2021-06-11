package org.ffpy.socketforward

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter

fun getCommandParam(args: Array<out String>): CommandParam {
    val param = CommandParam()
    JCommander.newBuilder()
        .addObject(param)
        .build()
        .parse(*args)
    return param
}

data class CommandParam(
    @Parameter(names = ["-config"], description = "配置文件路径")
    var config: String = "config.json5",
)