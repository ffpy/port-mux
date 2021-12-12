package org.ffpy.portmux

import org.ffpy.portmux.commandparam.CommandParams
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.server.ForwardServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

fun main(vararg args: String) {
    val log: Logger = LoggerFactory.getLogger("org.ffpy.portmux.MainKt")

    try {
        CommandParams.init(args)
        Configs.init(Path.of(CommandParams.param.config))

        ForwardServer.start()
    } catch (e: Exception) {
        log.error(e.message, e)
    }
}

