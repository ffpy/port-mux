package org.ffpy.portmux.server

import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.handler.codec.ByteToMessageDecoder
import org.ffpy.portmux.client.ClientManager
import org.ffpy.portmux.config.ForwardConfig
import org.ffpy.portmux.logger.LoggerManger
import org.ffpy.portmux.matcher.Matcher
import org.slf4j.LoggerFactory
import java.net.SocketAddress
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 匹配处理器
 */
class MatchHandler(private val config: ForwardConfig) : ByteToMessageDecoder() {
    companion object {
        private val log = LoggerFactory.getLogger(MatchHandler::class.java)
    }

    /** 读取超时定时器 */
    private var readTimeout: ScheduledFuture<*>? = null

    /** 匹配超时定时器 */
    private var matchTimeout: ScheduledFuture<*>? = null

    /** 匹配器 */
    private val matcher = Matcher(config.protocols)

    /** 是否正在连接客户端 */
    private var connecting = false

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()} 新连接")
        createReadTimeout(ctx)
        createMatchTimeout(ctx)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        LoggerManger.logData(log, msg, ctx.channel().remoteAddress())
        cancelReadTimeout()

        if (connecting) return

        val result = matcher.match(msg, ctx.channel().remoteAddress(), config.defaultAddress)
        if (!result.finish) return

        cancelMatchTimeout()

        if (result.address == null) {
            log.info("默认转发地址为空，关闭连接")
            ctx.close()
        } else {
            connectClient(result.address, ctx.channel(), ctx)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        cancelReadTimeout()
        cancelMatchTimeout()
        ctx.fireChannelInactive()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${ctx.channel().remoteAddress()}发生错误", cause)
        ctx.close()
    }

    /**
     * 连接转发地址
     *
     * @param address 转发地址
     * @param serverChannel 源连接
     * @param ctx ChannelHandlerContext
     */
    private fun connectClient(address: SocketAddress, serverChannel: Channel, ctx: ChannelHandlerContext) {
        connecting = true
        if (!serverChannel.isActive) return

        log.info("${serverChannel.remoteAddress()} => $address")

        ClientManager.connect(serverChannel, address, config.connectTimeout).addListener(ChannelFutureListener { f ->
            if (!serverChannel.isActive) {
                f.channel().close()
                return@ChannelFutureListener
            }

            if (f.isSuccess) {
                ctx.pipeline().replace(this, "forwardHandler", ForwardHandler(f.channel()))
            } else {
                log.error("连接${address}失败")
                serverChannel.close()
            }
        })
    }

    /**
     * 创建读取超时定时器
     */
    private fun createReadTimeout(ctx: ChannelHandlerContext) {
        readTimeout = ctx.channel().eventLoop().schedule({
            cancelMatchTimeout()
            timeout("等待数据", ctx)
        }, config.readTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    /**
     * 创建匹配超时定时器
     */
    private fun createMatchTimeout(ctx: ChannelHandlerContext) {
        matchTimeout = ctx.channel().eventLoop().schedule({
            cancelReadTimeout()
            timeout("匹配", ctx)
        }, config.matchTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    private fun timeout(name: String, ctx: ChannelHandlerContext) {
        if (connecting) return
        val address = config.readTimeoutAddress
        if (address == null) {
            log.info("${name}超时，没有配置超时转发地址，关闭连接")
            ctx.close()
        } else {
            log.info("${name}超时，转发到超时转发地址")
            connectClient(address, ctx.channel(), ctx)
        }
    }

    /**
     * 关闭读取超时定时器
     */
    private fun cancelReadTimeout() {
        readTimeout?.let {
            it.cancel(true)
            readTimeout = null
        }
    }

    /**
     * 关闭匹配超时定时器
     */
    private fun cancelMatchTimeout() {
        matchTimeout?.let {
            it.cancel(true)
            matchTimeout = null
        }
    }
}