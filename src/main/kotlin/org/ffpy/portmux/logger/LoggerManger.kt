package org.ffpy.portmux.logger

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import org.ffpy.portmux.App
import org.ffpy.portmux.config.ConfigManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.SocketAddress

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

        val debug = ConfigManager.config.logDataType
        if (debug.isNotEmpty()) {
            val data = Type.of(debug).apply(buf)
            log.debug("{} 发送数据({}): {}", address, buf.readableBytes(), data)
        }
    }

    private fun refreshLevel() {
        setLevel(Level.toLevel(ConfigManager.config.logLevel))
    }

    private fun setLevel(level: Level) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.getLogger(PACKAGE_NAME).level = level
    }

    enum class Type(val code: String, private val action: (ByteBuf) -> String) {
        STRING("string", { it.toString(Charsets.UTF_8) }),
        BYTE("byte", { ByteBufUtil.getBytes(it).contentToString() }),
        HEX("hex", { ByteBufUtil.hexDump(it) }),
        PRETTY_HEX("pretty_hex", { "\n" + ByteBufUtil.prettyHexDump(it) }),
        ;

        companion object {
            fun of(code: String): Type {
                for (value in values()) {
                    if (value.code == code) return value
                }
                throw Exception("debug不支持此参数: $code")
            }
        }

        fun apply(buf: ByteBuf) = action(buf)
    }
}