package org.ffpy.portmux

import org.ffpy.portmux.commandparam.CommandParamManager
import org.ffpy.portmux.config.ConfigManager
import org.ffpy.portmux.config.ForwardConfigManager
import org.ffpy.portmux.logger.LoggerManger
import org.ffpy.portmux.server.ForwardServer
import org.ffpy.portmux.server.WatchServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.system.exitProcess

class App

fun main(vararg args: String) {
    val log: Logger = LoggerFactory.getLogger("main")

    try {
        CommandParamManager.init(args)
        val configPath = Paths.get(CommandParamManager.param.config)

        ConfigManager.init(configPath)
        LoggerManger.init()
        ForwardConfigManager.init()

        if (CommandParamManager.param.watchConfig) {
            WatchServer().start(configPath)
        }

        ForwardServer().start()
    } catch (e: Exception) {
        log.error(e.message)
        exitProcess(-1)
    }
}
