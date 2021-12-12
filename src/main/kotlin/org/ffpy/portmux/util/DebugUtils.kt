package org.ffpy.portmux.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.ffpy.portmux.commandparam.CommandParams.param
import org.slf4j.Logger

/**
 * Debug工具类
 */
object DebugUtils {

    /**
     * 打印转发数据，只有在命令行加了-debug参数后才会打印
     *
     * @param log [Logger]
     * @param buf 要打印的数据
     * @param ctx 发送数据的对象
     */
    fun logData(log: Logger, buf: ByteBuf, ctx: ChannelHandlerContext) {
        val debug = param.debug
        if (debug.isNotEmpty()) {
            val data = when (debug) {
                "string" -> buf.toString(Charsets.UTF_8)
                "byte" -> ByteBufUtils.getBytes(buf).contentToString()
                else -> throw Exception("-debug不支持此参数: $debug")
            }
            log.info("{}数据({}): {}", ctx.channel().remoteAddress(), buf.readableBytes(), data)
        }
    }
}