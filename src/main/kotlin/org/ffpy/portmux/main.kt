package org.ffpy.portmux

import org.ffpy.portmux.commandparam.CommandParams
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.server.ForwardServer
import org.ffpy.portmux.server.WatchServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.*
import kotlin.system.exitProcess

fun main(vararg args: String) {
    val log: Logger = LoggerFactory.getLogger("main")

    try {
        CommandParams.init(args)
        val configPath = Path.of(CommandParams.param.config)

        Configs.init(configPath)
        if (CommandParams.param.watchConfig) {
            WatchServer.start(configPath)
        }
        ForwardServer.start()
    } catch (e: Exception) {
        log.error(e.message)
        exitProcess(-1)
    }
}
