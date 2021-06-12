package org.ffpy.socketforward

import org.ffpy.socketforward.commandparam.CommandParams
import org.ffpy.socketforward.config.Configs
import org.ffpy.socketforward.server.ForwardServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

fun main(vararg args: String) {
    val log: Logger = LoggerFactory.getLogger("org.ffpy.socketforward.MainKt")

    try {
        CommandParams.init(args)
        Configs.init(Path.of(CommandParams.param.config))

        ForwardServer.start()
    } catch (e: Exception) {
        log.error(e.message, e)
    }
}

