package org.ffpy.socketforward.util

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.ffpy.socketforward.commandparam.CommandParams.param
import org.slf4j.Logger

object DebugUtils {

    fun debugData(log: Logger, buf: ByteBuf, ctx: ChannelHandlerContext) {
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