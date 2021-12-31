package org.ffpy.portmux.logger

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import io.netty.buffer.ByteBuf
import org.ffpy.portmux.App
import org.ffpy.portmux.config.ConfigManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.SocketAddress
import kotlin.math.min

object LoggerManger {

    private val PACKAGE_NAME = App::class.java.`package`.name

    fun init() {
        refreshLevel()
        ConfigManager.addOnChangedListener { refreshLevel() }
    }

    /**
     * 打印转发数据
     *
     * @param log [Logger]
     * @param buf 要打印的数据
     * @param address 发送数据的地址
     */
    fun logData(log: Logger, buf: ByteBuf, address: SocketAddress?) {
        if (!log.isDebugEnabled) return

        val config = ConfigManager.config
        val debug = config.logDataType
        if (debug.isNotEmpty()) {
            val sliceBuf = buf.slice(buf.readerIndex(), min(buf.readableBytes(), config.logDataLen))
            val data = LogDataType.of(debug).apply(sliceBuf)
            log.debug("{} send({}): {}", address, buf.readableBytes(), data)
        }
    }

    private fun refreshLevel() {
        setLevel(Level.toLevel(ConfigManager.config.logLevel, Level.INFO))
    }

    private fun setLevel(level: Level) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.getLogger(PACKAGE_NAME).level = level
    }

}