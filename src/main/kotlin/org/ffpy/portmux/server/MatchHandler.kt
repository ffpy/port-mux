package org.ffpy.portmux.server

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
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
class MatchHandler(private val config: ForwardConfig) : ChannelInboundHandlerAdapter() {
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

    /** 已经读取到的所有数据 */
    private lateinit var readBuf: CompositeByteBuf

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()} 新连接")
        readBuf = ctx.alloc().compositeBuffer()

        createReadTimeout(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        cancelReadTimeout()
        matchTimeout ?: createMatchTimeout(ctx)

        LoggerManger.logData(log, msg as ByteBuf, ctx.channel().remoteAddress())

        readBuf.addComponent(true, msg)

        if (connecting) return

        val result = matcher.match(readBuf, ctx.channel().remoteAddress(), config.defaultAddress)
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
        log.info("${ctx.channel().remoteAddress()} 连接断开")
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

        ctx.channel().config().isAutoRead = false

        log.info("${serverChannel.remoteAddress()} => $address")

        ClientManager.connect(serverChannel, address, config.connectTimeout).addListener(ChannelFutureListener { f ->
            if (!serverChannel.isActive) {
                f.channel().close()
                return@ChannelFutureListener
            }

            if (f.isSuccess) {
                log.info("${f.channel().remoteAddress()} 客户端连接成功")

                ctx.pipeline().replace(this, "forwardHandler", ForwardHandler(f.channel()))
                if (readBuf.readableBytes() > 0) {
                    ctx.pipeline()
                        .fireChannelRead(readBuf)
                        .fireChannelReadComplete()
                } else {
                    readBuf.release()
                }
                ctx.channel().config().isAutoRead = true
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
            if (connecting) return@schedule

            val address = config.readTimeoutAddress
            if (address == null) {
                log.info("等待数据超时，没有配置超时转发地址，关闭连接")
                ctx.close()
            } else {
                log.info("等待数据超时，转发到超时转发地址")
                connectClient(address, ctx.channel(), ctx)
            }
        }, config.readTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    /**
     * 创建匹配超时定时器
     */
    private fun createMatchTimeout(ctx: ChannelHandlerContext) {
        matchTimeout = ctx.channel().eventLoop().schedule({
            cancelReadTimeout()
            if (connecting) return@schedule

            val address = config.defaultAddress
            if (address == null) {
                log.info("匹配超时，没有配置默认转发地址，关闭连接")
                ctx.close()
            } else {
                log.info("匹配超时，转发到默认转发地址")
                connectClient(address, ctx.channel(), ctx)
            }
        }, config.matchTimeout.toLong(), TimeUnit.MILLISECONDS)
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