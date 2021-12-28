package org.ffpy.portmux.server

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.ffpy.portmux.util.DebugUtils
import org.slf4j.LoggerFactory

/**
 * 转发处理器
 *
 * @param clientChannel 目标Channel
 */
class ForwardHandler(private val clientChannel: Channel) : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ForwardHandler::class.java)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        DebugUtils.logData(log, msg as ByteBuf, ctx)
        clientChannel.write(msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        clientChannel.flush()
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        clientChannel.config()?.isAutoRead = ctx.channel().isWritable
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()}连接断开")
        clientChannel.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${ctx.channel().remoteAddress()}发生错误", cause)
        ctx.close()
    }
}