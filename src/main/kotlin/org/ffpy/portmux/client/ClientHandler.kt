package org.ffpy.portmux.client

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.ffpy.portmux.logger.LoggerManger
import org.slf4j.LoggerFactory

/**
 * 目标连接处理器
 *
 * @param serverChannel 对应的源连接
 */
class ClientHandler(private val serverChannel: Channel) : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ClientHandler::class.java)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()} 客户端连接成功")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()} 客户端连接断开")
        serverChannel.close()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        LoggerManger.logData(log, msg as ByteBuf, ctx.channel().remoteAddress())
        serverChannel.write(msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        serverChannel.flush()
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        serverChannel.config().isAutoRead = ctx.channel().isWritable
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${serverChannel.remoteAddress()} 发生错误: ${cause.message}", cause)
        ctx.close()
    }
}