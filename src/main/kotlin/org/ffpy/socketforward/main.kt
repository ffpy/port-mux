package org.ffpy.socketforward

import org.ffpy.socketforward.util.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

val log: Logger = LoggerFactory.getLogger("org.ffpy.socketforward.MainKt")

fun main(vararg args: String) {
    val param = getCommandParam(args)
    val configPath = Path.of(param.config)
    if (!configPath.toFile().exists()) {
        log.error("找不到配置文件")
        return
    }
    val config = JsonUtils.parse(configPath, Config::class.java)
    log.info(config.toString())
}


