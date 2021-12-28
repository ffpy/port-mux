package org.ffpy.portmux.server

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.ffpy.portmux.client.ClientHandler
import org.ffpy.portmux.config.ForwardConfig
import org.ffpy.portmux.protocol.Matcher
import org.ffpy.portmux.util.DebugUtils
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

    /** 首次读取超时检查定时器 */
    private var firstReadTimeout: ScheduledFuture<*>? = null

    /** 待匹配数据 */
    private var firstData: ByteBuf? = null

    private val matcher = Matcher(config.protocols)

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()}新连接")

        // 连接后一段时间内没有数据则直接转发到默认地址
        createFirstTimeout(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        cancelFirstTimeout()

        DebugUtils.logData(log, msg as ByteBuf, ctx)

        val result = matcher.match(msg, ctx.channel().remoteAddress(), config.defaultAddress)
        if (!result.finish) return

        if (result.address == null) {
            log.info("默认转发地址为空，关闭连接")
            ctx.close()
        } else {
            connectClient(result.address, msg, ctx.channel(), ctx)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        cancelFirstTimeout()
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
     * @param msg 首次读取的数据
     * @param serverChannel 源连接
     */
    private fun connectClient(address: SocketAddress, msg: Any?, serverChannel: Channel, ctx: ChannelHandlerContext) {
        if (!serverChannel.isActive) return

        log.info("${serverChannel.remoteAddress()} => $address")

        Bootstrap()
            .channel(NioSocketChannel::class.java)
            .group(serverChannel.eventLoop())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.connectTimeout)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(ClientHandler(serverChannel))
                }
            })
            .connect(address)
            .addListener(ChannelFutureListener { f ->
                if (f.isSuccess) {
                    ctx.pipeline().replace(this, "forwardHandler", ForwardHandler(f.channel()))
                    msg?.let { ctx.pipeline().fireChannelRead(it).fireChannelReadComplete() }
                } else {
                    log.error("连接${address}失败")
                    serverChannel.close()
                }
            })
    }

    /**
     * 创建首次读取超时检查定时器
     */
    private fun createFirstTimeout(ctx: ChannelHandlerContext) {
        firstReadTimeout = ctx.channel().eventLoop().schedule({
            val address = config.timeoutAddress
            if (address == null) {
                log.info("等待数据超时，没有配置超时转发地址，关闭连接")
                ctx.close()
            } else {
                log.info("等待数据超时，转发到超时转发地址")
                connectClient(address, null, ctx.channel(), ctx)
            }
        }, config.config.readTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    /**
     * 关闭首次读取超时检查定时器
     */
    private fun cancelFirstTimeout() {
        firstReadTimeout?.let {
            it.cancel(true)
            firstReadTimeout = null
        }
    }
}