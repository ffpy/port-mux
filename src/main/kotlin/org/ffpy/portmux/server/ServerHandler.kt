package org.ffpy.portmux.server

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.HashedWheelTimer
import io.netty.util.Timeout
import org.ffpy.portmux.client.ClientHandler
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.config.ForwardConfig
import org.ffpy.portmux.util.ByteBufUtils
import org.ffpy.portmux.util.DebugUtils
import org.slf4j.LoggerFactory
import java.net.SocketAddress
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * 转发处理器
 */
class ServerHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ServerHandler::class.java)

        /** 定时器 */
        private val timer = HashedWheelTimer(100, TimeUnit.MILLISECONDS, 64)

        private var forwardConfig = ForwardConfig(Configs.config)

        fun refreshForwardConfig() {
            forwardConfig = ForwardConfig(Configs.config)
        }
    }

    /** 转发目标连接 */
    private var clientChannel: Channel? = null

    /** 首次读取超时检查定时器 */
    private var firstReadTimeout: Timeout? = null

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()}新连接")

        // 连接后一段时间内没有数据则直接转发到默认地址
        firstReadTimeout = timer.newTimeout({
            val address = forwardConfig.timeoutAddress
            if (address == null) {
                log.info("等待数据超时，没有配置超时转发地址，关闭连接")
                ctx.close()
            } else {
                log.info("等待数据超时，转发到超时转发地址")
                connectClient(address, null, ctx.channel())
            }
        }, forwardConfig.config.readTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        cancelFirstTimeout()

        DebugUtils.logData(log, msg as ByteBuf, ctx)

        val c = clientChannel
        if (c == null) {
            val address = matchProtocol(msg, ctx)
            if (address == null) {
                log.info("默认转发地址为空，关闭连接")
                ctx.close()
            } else {
                connectClient(address, msg, ctx.channel())
            }
        } else {
            c.write(msg)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        clientChannel?.flush()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()}连接断开")
        cancelFirstTimeout()
        clientChannel?.close()
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        clientChannel?.config()?.isAutoRead = ctx.channel().isWritable
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
    private fun connectClient(address: SocketAddress, msg: Any?, serverChannel: Channel) {
        if (!serverChannel.isActive) return

        log.info("${serverChannel.remoteAddress()} => $address")

        val future = Bootstrap()
            .channel(NioSocketChannel::class.java)
            .group(serverChannel.eventLoop())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Configs.config.connectTimeout)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(ClientHandler(serverChannel))
                }
            })
            .connect(address)

        clientChannel = future.channel()
        future.addListener(ChannelFutureListener { f ->
                if (f.isSuccess) {
                    msg?.let { f.channel().writeAndFlush(it) }
                } else {
                    log.error("连接${address}失败")
                    serverChannel.close()
                }
            })
    }

    /**
     * 关闭首次读取超时检查定时器
     */
    private fun cancelFirstTimeout() {
        firstReadTimeout?.let {
            it.cancel()
            firstReadTimeout = null
        }
    }

    /**
     * 匹配转发地址
     */
    private fun matchProtocol(buf: ByteBuf, ctx: ChannelHandlerContext): SocketAddress? {
        val data = ByteBufUtils.getBytes(buf, min(forwardConfig.maxLength, buf.readableBytes()))
        for (protocol in forwardConfig.protocols) {
            if (protocol.match(data)) {
                log.info("{}匹配协议: {}", ctx.channel().remoteAddress(), protocol.name)
                return protocol.address
            }
        }

        log.info("{}匹配失败，转发到默认地址", ctx.channel().remoteAddress())
        return forwardConfig.defaultAddress
    }
}