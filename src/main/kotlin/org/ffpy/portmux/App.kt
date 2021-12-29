package org.ffpy.portmux

import org.ffpy.portmux.commandparam.CommandParams
import org.ffpy.portmux.config.ConfigManager
import org.ffpy.portmux.config.ForwardConfigManager
import org.ffpy.portmux.logger.LoggerManger
import org.ffpy.portmux.server.ForwardServer
import org.ffpy.portmux.server.WatchServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.system.exitProcess

class App

fun main(vararg args: String) {
    val log: Logger = LoggerFactory.getLogger("main")

    try {
        CommandParams.init(args)
        val configPath = Path.of(CommandParams.param.config)

        ConfigManager.init(configPath)
        LoggerManger.init()
        ForwardConfigManager.init()
        if (CommandParams.param.watchConfig) {
            WatchServer.start(configPath)
        }

        ForwardServer.start()
    } catch (e: Exception) {
        log.error(e.message)
        exitProcess(-1)
    }
}
