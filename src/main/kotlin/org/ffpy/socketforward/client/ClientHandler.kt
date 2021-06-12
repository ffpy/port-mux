package org.ffpy.socketforward.client

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.ffpy.socketforward.util.DebugUtils
import org.slf4j.LoggerFactory

/**
 * 目标连接处理器
 */
class ClientHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ClientHandler::class.java)
    }

    /** 对应的源连接 */
    private lateinit var serverChannel: Channel

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        log.info("${ctx.channel().remoteAddress()}连接成功")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        log.info("${ctx.channel().remoteAddress()}连接断开")
        serverChannel.close()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        DebugUtils.logData(log, msg as ByteBuf, ctx)
        serverChannel.writeAndFlush(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${serverChannel.remoteAddress()}发生错误: ${cause.message}", cause)
        ctx.close()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any?) {
        // 绑定源连接
        if (evt is Channel) {
            serverChannel = evt
        }
    }
}