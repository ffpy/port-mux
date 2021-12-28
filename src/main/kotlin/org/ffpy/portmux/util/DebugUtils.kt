package org.ffpy.portmux.util

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import org.ffpy.portmux.config.Configs
import org.slf4j.Logger

/**
 * Debug工具类
 */
object DebugUtils {

    enum class Type(val code: String, private val action: (ByteBuf) -> String) {
        STRING("string", { it.toString(Charsets.UTF_8) }),
        HEX("hex", { ByteBufUtil.hexDump(it) }),
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

    /**
     * 打印转发数据，只有在配置文件中配置了debug参数后才会打印
     *
     * @param log [Logger]
     * @param buf 要打印的数据
     * @param ctx 发送数据的对象
     */
    fun logData(log: Logger, buf: ByteBuf, ctx: ChannelHandlerContext) {
        val debug = Configs.config.debug
        if (debug.isNotEmpty()) {
            val data = Type.of(debug).apply(buf)
            log.info("{}数据({}): {}", ctx.channel().remoteAddress(), buf.readableBytes(), data)
        }
    }
}