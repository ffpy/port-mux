package org.ffpy.socketforward

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

val log: Logger = LoggerFactory.getLogger("org.ffpy.socketforward.MainKt")

fun main(vararg args: String) {
    val param = getCommandParam(args)
    val config = getConfig(Path.of(param.config)) ?: return
    log.info(config.toString())
}
